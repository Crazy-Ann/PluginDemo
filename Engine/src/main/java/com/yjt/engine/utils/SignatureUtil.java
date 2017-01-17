package com.yjt.engine.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.compat.BuildConfig;
import android.text.TextUtils;

import com.yjt.engine.constant.Constant;
import com.yjt.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SignatureUtil {

    private static SignatureUtil mInstance;

    private SignatureUtil() {
        // cannot be instantiated
    }

    public static synchronized SignatureUtil getInstance() {
        if (mInstance == null) {
            mInstance = new SignatureUtil();
        }
        return mInstance;
    }

    public static synchronized void releaseInstance() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    @Nullable
    @SuppressLint("PackageManagerGetSignatures")
    public Signature[] getSignatures(Context context) {
        Signature[] signatures = null;
        try {
            PackageInfo pkgInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            signatures = pkgInfo.signatures;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            LogUtil.getInstance().println("Can not get signature, error = " + e.getLocalizedMessage());
        }
        return signatures;
    }

    /**
     * 获取指定文件的签名
     */
    @Nullable
    public Signature[] getSignatures(Context context, String apkPath) {
        Signature[] signatures = getArchiveSignatures(context, apkPath);
        if (signatures == null) {
            signatures = getArchiveSignatures(apkPath, false);
            if (signatures == null) {
                signatures = getArchiveSignatures(apkPath, true);
            }
        }
        return signatures;
    }


    @SuppressLint("PackageManagerGetSignatures")
    private Signature[] getArchiveSignatures(Context context, String apkPath) {
        PackageInfo info
                = context.getPackageManager().getPackageArchiveInfo(apkPath,
                                                                    PackageManager.GET_SIGNATURES);
        return info == null ? null : info.signatures;
    }

    /**
     * 获取指定文件的签名
     */
    @Nullable
    public Signature[] getArchiveSignatures(String apkPath, boolean simpleMode) {
        Signature signatures[];
        JarFile jarFile = null;

        try {
            byte[] readBuffer = new byte[Constant.Space.IO_BUFFER_CAPACITY];
            jarFile = new JarFile(apkPath);
            Certificate[] certs = null;
            if (simpleMode) {
                // if SIMPLE MODE,, then we
                // can trust it...  we'll just use the AndroidManifest.xml
                // to retrieve its signatures, not validating all of the
                // files.
                JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");
                certs = loadCertificates(jarFile, jarEntry, readBuffer);
                if (certs == null) {
                    LogUtil.getInstance().println("Package "
                                                          + " has no certificates at entry "
                                                          + jarEntry.getName() + "; ignoring!");
                    LogUtil.getInstance().println("INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                    return null;
                }
                if (BuildConfig.DEBUG) {
                    LogUtil.getInstance().println("File " + apkPath + ": entry=" + jarEntry + " certs=" + certs.length);
                    for (Certificate cert : certs) {
                        LogUtil.getInstance().println("Public key: "
                                                              + Arrays.toString(cert.getPublicKey().getEncoded())
                                                              + " " + cert.getPublicKey());
                    }
                }
            } else {
                Enumeration entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry je = (JarEntry) entries.nextElement();
                    if (je.isDirectory()) continue;
                    if (je.getName().startsWith("META-INF/")) continue;
                    Certificate[] localCerts = loadCertificates(jarFile, je,
                                                                readBuffer);
                    if (BuildConfig.DEBUG) {
                        LogUtil.getInstance().println("File " + apkPath + " entry " + je.getName()
                                                              + ": certs=" + Arrays.toString(certs) + " ("
                                                              + (certs != null ? certs.length : 0) + ")");
                    }
                    if (localCerts == null) {
                        LogUtil.getInstance().println("Package "
                                                              + " has no certificates at entry "
                                                              + je.getName() + "; ignoring!");
                        LogUtil.getInstance().println("INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                        return null;
                    } else if (certs == null) {
                        certs = localCerts;
                    } else {
                        // Ensure all certificates match.
                        for (Certificate cert : certs) {
                            boolean found = false;
                            for (Certificate localCert : localCerts) {
                                if (cert != null && cert.equals(localCert)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found || certs.length != localCerts.length) {
                                LogUtil.getInstance().println("Package "
                                                                      + " has mismatched certificates at entry "
                                                                      + je.getName() + "; ignoring!");
                                LogUtil.getInstance().println("INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES");
                                return null;
                            }
                        }
                    }
                }
            }
            if (certs != null && certs.length > 0) {
                signatures = new Signature[certs.length];
                for (int i = 0; i < certs.length; i++) {
                    signatures[i] = new Signature(certs[i].getEncoded());
                }
            } else {
                LogUtil.getInstance().println("Package " + " has no certificates; ignoring!");
                LogUtil.getInstance().println("INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                return null;
            }
        } catch (CertificateEncodingException e) {
            LogUtil.getInstance().println("Exception reading " + apkPath);
            LogUtil.getInstance().println("INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            LogUtil.getInstance().println("Exception reading " + apkPath);
            LogUtil.getInstance().println("INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING");
            e.printStackTrace();
            return null;
        } catch (RuntimeException e) {
            LogUtil.getInstance().println("Exception reading " + apkPath);
            LogUtil.getInstance().println("INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION");
            e.printStackTrace();
            return null;
        } finally {
            if (jarFile != null) {
                FileUtil.getInstance().closeQuietly(jarFile);
            }
        }
        return signatures;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    private Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        InputStream in = null;
        try {
            // We must read the stream for the JarEntry to retrieve
            // its certificates.
            in = new BufferedInputStream(jarFile.getInputStream(je));
            while (in.read(readBuffer, 0, readBuffer.length) != -1) {
                // Do nothing.
            }
            return je != null ? je.getCertificates() : null;

        } catch (IOException | RuntimeException e) {
            LogUtil.getInstance().println("Exception reading " + je.getName() + " in " + jarFile.getName());
            e.printStackTrace();
        } finally {
            FileUtil.getInstance().closeQuietly(in);
        }
        return null;
    }

    public boolean isSignaturesSame(Signature[] s1, Signature[] s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        if (s1.length != s2.length) {
            return false;
        }

        HashSet<Signature> set1 = new HashSet<>();
        Collections.addAll(set1, s1);
        HashSet<Signature> set2 = new HashSet<>();
        Collections.addAll(set2, s2);

        // Make sure s2 contains all signatures in s1.
        return set1.equals(set2);
    }

    public boolean isSignaturesSame(String s1, Signature[] s2) {
        if (TextUtils.isEmpty(s1)) {
            return false;
        }
        if (s2 == null) {
            return false;
        }

        for (Signature signature : s2) {
            String item = signature.toCharsString().toLowerCase();
            if (item.equalsIgnoreCase(s1)) {
                return true;
            }
        }
        return false;
    }

    public void printSignature(Signature[] s) {
        if (s == null || s.length == 0) {
            LogUtil.getInstance().println("Signature is empty.");
        } else {
            int length = s.length;
            for (int i = 0; i < length; i++) {
                LogUtil.getInstance().println("Signature " + i + " = " + s[i].toCharsString().toLowerCase());
            }
        }
    }
}
