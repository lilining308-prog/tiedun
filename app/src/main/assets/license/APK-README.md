# APK 分发说明

tiedun 铁盾按木兰宽松许可证第 2 版（Mulan PSL v2，SPDX: MulanPSL-2.0）发布。本文件随 APK 打包，用于说明分发要求与源码对应关系。

## 分发目录

| 目录 | 用途 |
|------|------|
| `apk/stable/` | 稳定构建 |
| `apk/debug/` | 开发测试构建，普通用户勿装 |
| `apk/archive/` | 历史版本归档 |
| `deliverables/` | 本地交付构建 |

MulanPSL-2.0 不强制提供源码，但本项目承诺开源：分发 APK 时请同时提供与该 APK 对应的完整源码、`LICENSE-MulanPSL-2.0.txt`、`NOTICE`、`THIRD_PARTY_NOTICES.md`。文件名或版本号不能单独证明源码对应关系，应附 SHA-256 与对应源码提交或源码压缩包。

以 GPL-3.0 发布的历史版本授权永久有效，其源码提供义务按当时 GPL-3.0 条款继续履行。

## 源码公开

- 主源码：`app/src/main/java/com/lilining/lockescape/`
- 测试源码：`app/src/test/java/com/lilining/lockescape/`
- 主许可证：MulanPSL-2.0，全文见仓库根目录 `LICENSE`（随包副本 `LICENSE-MulanPSL-2.0.txt`）
- 第三方声明：`THIRD_PARTY_NOTICES.md` 与 `NOTICE`
- 传递依赖清单：`docs/DEPENDENCY_LICENSES.md`

所有原创源码、资源、构建脚本与文档均按 MulanPSL-2.0 随项目公开。包含本文件的 APK 在 `assets/license/` 内置许可材料。

## 权限说明

铁盾申请的权限与其功能直接相关：

| 权限 | 用途 |
|------|------|
| 无障碍服务 | 前台应用识别、按键触发 |
| 前台服务 / 通知 | 保活与运行状态 |
| 悬浮窗 | 全屏处理界面 |
| 查询应用列表 | 目标识别与白名单 |
| Shizuku API | 特权命令通道 |
| 开机完成 | 守护恢复 |
| 电池优化豁免 | 后台稳定运行 |
