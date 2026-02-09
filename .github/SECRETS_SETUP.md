# Setting Up GitHub Secrets for CI/CD

## Required Secret: GOOGLE_SERVICES_JSON

The Android CI pipeline requires the Firebase configuration file (`google-services.json`) to build successfully. Since this file contains sensitive credentials, it should not be committed to the repository.

### How to Set Up the Secret

1. **Get the content of your google-services.json file**:
   - Open `android-app/app/google-services.json` in a text editor
   - Copy the entire contents of the file

2. **Add the secret to GitHub**:
   - Go to your repository on GitHub
   - Navigate to **Settings** → **Secrets and variables** → **Actions**
   - Click **New repository secret**
   - Name: `GOOGLE_SERVICES_JSON`
   - Value: Paste the entire contents of your `google-services.json` file
   - Click **Add secret**

### How It Works

The Android workflow has a step that creates the `google-services.json` file from the secret before building:

```yaml
- name: Create google-services.json
  run: echo '${{ secrets.GOOGLE_SERVICES_JSON }}' > android-app/app/google-services.json
```

This ensures your Firebase credentials remain secure while still allowing the CI pipeline to build successfully.

### Verification

After adding the secret, push any changes to trigger the workflow and verify it completes successfully without the "File google-services.json is missing" error.
