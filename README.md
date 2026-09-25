# 👋🧩 Plyrs1 Morphe Patches

Repository for custom Morphe patches by Plyrs1.

## ❓ About

Custom patches for Android applications.

### How to use these patches

Click here to add these patches to Morphe: https://morphe.software/add-source?github=Plyrs1/morphe-patches

## 🩹 Patches list

<!-- PATCHES_START EXPANDED -->
> **[v1.3.0-dev.2](https://github.com/Plyrs1/morphe-patches/releases/tag/v1.3.0-dev.2)**&nbsp;&nbsp;•&nbsp;&nbsp;`dev`&nbsp;&nbsp;•&nbsp;&nbsp;7 patches total
<details open>
<summary>📦 Struk Pom / SPBU&nbsp;&nbsp;•&nbsp;&nbsp;3 patches</summary>
<br>

**🎯 Supported versions:**

| 1.2.7 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Bypass Review Dialog](#bypass-review-dialog) | Bypasses the in-app review confirmation dialog when pressing Back on the Home screen, allowing immediate exit. |  |
| [Hide Premium Button](#hide-premium-button) | Hides the Premium upgrade button from both the top action bar on the Home screen and the Settings menu. |  |
| [Remove Ads](#remove-ads) | Removes all banner and interstitial ads by unlocking ad-free premium status and disabling AdMob loaders. |  |

</details>

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

<details open>
<summary>📦 Pertaminiku&nbsp;&nbsp;•&nbsp;&nbsp;1 patch</summary>
<br>

**🎯 Supported versions:**

| 1.0.2 |
| :---: |

| 💊&nbsp;Patch | 📜&nbsp;Description | ⚙️&nbsp;Options |
|----------|----------------|-----------|
| [Lifetime Premium](#lifetime-premium) | Unlocks lifetime premium: removes watermark forever, disables ads, unlocks all templates. |  |

</details>

<!-- PATCHES_END -->

### 🛠️ Building locally

- Run `./gradlew buildAndroid`
- The built patches .mpp file is found in `patches/build/libs/patches-*.mpp`
- Patch the mpp file using [Morphe-Desktop](https://github.com/MorpheApp/morphe-desktop)
  like any other patch bundle.

See the [Morphe documentation](https://github.com/MorpheApp/morphe-documentation) for more information.

## 📜 License

Plyrs1 Morphe Patches are licensed under the [GNU General Public License v3.0](LICENSE)
