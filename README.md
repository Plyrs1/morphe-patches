# 👋🧩 Plyrs1 Morphe Patches

Repository for custom Morphe patches by Plyrs1.

## ❓ About

Custom patches for Android applications.

### How to use these patches

Click here to add these patches to Morphe: https://morphe.software/add-source?github=Plyrs1/morphe-patches

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.2.0](https://github.com/Plyrs1/morphe-patches/releases/tag/v1.2.0)**&nbsp;&nbsp;•&nbsp;&nbsp;`main`&nbsp;&nbsp;•&nbsp;&nbsp;3 patches total
<details open>
<summary>📦 Struk POM&nbsp;&nbsp;•&nbsp;&nbsp;3 patches</summary>
<br>

**🎯 Supported versions:**

| 1.17.057 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Disable Startup Permissions](#disable-startup-permissions) | Suppresses indiscriminate startup requests for camera and storage permissions, and configures manifest for direct system camera capture. |  |
| [Hide Gift Button](#hide-gift-button) | Hides the gift / donation button in the top action bar of the main menu. |  |
| [Remove All Ads](#remove-all-ads) | Disables all AdMob preloading, bypasses interstitial ads, and auto-removes the watermark on receipt screens without user interaction. |  |

</details>

<!-- PATCHES_END -->

### 🛠️ Building locally

- Run `./gradlew buildAndroid`
- The built patches .mpp file is found in `patches/build/libs/patches-*.mpp`
- Patch the mpp file using [Morphe-Desktop](https://github.com/MorpheApp/morphe-desktop)
  like any other patch bundle.

See the [Morphe documentation](https://github.com/MorpheApp/morphe-documentation) for more information.

## 📜 License

UserXYZ Patches are licensed under the [GNU General Public License v3.0](LICENSE)
