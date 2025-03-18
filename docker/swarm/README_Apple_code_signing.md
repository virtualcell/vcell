# Creating a Code Signing Certificate (.p12) from an Apple Developer ID Application Certificate

This guide provides step-by-step instructions to create a code signing certificate (.p12) from an Apple Developer ID Application certificate. This certificate is used to code sign your app for distribution outside of the Mac App Store Connect.

## Steps

### 1. Generate a Certificate Signing Request (CSR)
1. Open the Keychain Access application on your Mac.
2. From the menu bar, select `Keychain Access` > `Certificate Assistant` > `Request a Certificate from a Certificate Authority...`.
3. Enter your email address and common name. Leave the CA Email Address field blank.
4. Select `Saved to disk` and `Let me specify key pair information`.
5. Click `Continue` and save the CSR file to your disk.

### 2. Request the Developer ID Application Certificate
1. Go to the [Apple Developer](https://developer.apple.com/) website and log in with your Apple ID.
2. Navigate to `Certificates, Identifiers & Profiles`.
3. Under `Certificates`, click the `+` button to create a new certificate.
4. Select `Developer ID Application` and click `Continue`.
5. Upload the CSR file you generated in step 1 and click `Continue`.
6. Download the generated certificate (`.cer` file) to your disk.

### 3. Import the Certificate into Keychain Access
1. Double-click the downloaded `.cer` file to import it into Keychain Access.
2. The certificate should appear in the `My Certificates` category.

### 4. Export the Certificate as a .p12 File
1. In Keychain Access, locate the imported certificate under `My Certificates`.
2. Right-click the certificate and select `Export`.
3. Choose a location to save the file and select the `Personal Information Exchange (.p12)` format.
4. Enter a name for the file and click `Save`.
5. You will be prompted to create a password for the .p12 file. Enter a strong password and click `OK`.
6. Enter your Mac user account password to allow the export.

### 5. Verify the .p12 File
1. Ensure the .p12 file is saved at the specified location.
2. You can use this .p12 file for code signing your applications.

By following these steps, you will have created a .p12 code signing certificate from an Apple Developer ID Application certificate.