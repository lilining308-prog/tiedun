# 源码公开说明

本 APK 属于 tiedun 项目，主许可证为木兰宽松许可证第 2 版（Mulan PSL v2，SPDX: MulanPSL-2.0）。

## 源码直达路径

- 当前开发版主源码：`app/src/main/java/com/lilining/lockescape/`
- 当前开发版测试源码：`app/src/test/java/com/lilining/lockescape/`
- Android Manifest：`app/src/main/AndroidManifest.xml`
- 布局资源：`app/src/main/res/layout/`
- 颜色、主题、Drawable 资源：`app/src/main/res/values/`、`app/src/main/res/drawable/`
- Gradle 构建脚本：`build.gradle.kts`、`settings.gradle.kts`、`app/build.gradle.kts`

## 在线源码目录

以下 `main` 链接仅指向线上已提交版本；若分发的 APK 由其他提交或未提交工作区构建，须另行提供与该 APK 对应的完整源码及构建脚本，不应仅以此链接代替。

- 主源码：https://github.com/lilining308-prog/tiedun/tree/main/app/src/main/java
- 测试源码：https://github.com/lilining308-prog/tiedun/tree/main/app/src/test/java

## APK 内置许可文件

- `assets/license/LICENSE-MulanPSL-2.0.txt`
- `assets/license/NOTICE.txt`
- `assets/license/THIRD_PARTY_NOTICES.md`
- `assets/license/DEPENDENCY_LICENSES.md`（release 运行时传递依赖清单）
- `assets/license/README.md`
- `assets/license/APK-README.md`
- 历史二进制与源码溯源：仓库 `docs/APK源码对应表.md`（需按实际 APK 的 SHA-256 核对）

分发 APK 时本项目要求同时提供完整源码、MulanPSL-2.0 许可证和第三方许可声明。以 GPL-3.0 发布的历史版本另按其原有条款提供 GPL-3.0 文本与对应源码。

构建版本校验：分发方必须在 APK 下载页标出该二进制文件的 SHA-256，以及生成它的源码提交或同一发布位置的源码压缩包；仅凭 `versionName` 或固定 `main` 链接无法确认对应源码。
