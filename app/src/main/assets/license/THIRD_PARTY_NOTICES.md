# Third-Party Notices

本项目的设计实现参考了以下开源项目。所有上游项目的版权归其各自作者所有。
本声明依据各上游项目许可证要求提供，供本项目使用者与下游分发者遵守。

本项目以木兰宽松许可证第 2 版（Mulan PSL v2，SPDX: MulanPSL-2.0）发布，见 [LICENSE](LICENSE)。
经逐文件比对与反向指纹检索（见 [docs/许可合规审查报告.md](docs/许可合规审查报告.md)），本项目未复制、改写或翻译任何上游代码，不构成 GPL-3.0 上游项目的衍生作品；对下述 GPL-3.0 项目仅作能力设计参考，并保留致谢。

## 参考项目清单

| 能力块 | 上游项目 | 许可证 | 与本项目关系 |
|--------|---------|--------|------|
| 物理按键监听 | [F111111shhh/Extinguish](https://github.com/F111111shhh/Extinguish)（fork 自 [Moderpach/Extinguish](https://github.com/Moderpach/Extinguish)） | GPL-3.0 | 设计参考，未复制代码 |
| 全屏覆盖 | [coderlidy/Fake-Lock-Screen](https://github.com/coderlidy/Fake-Lock-Screen) | MIT | 设计参考，未复制代码 |
| root 权限通道 | [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku) | Apache-2.0 | 依赖引入（二进制），保留声明与许可全文 |
| 停用/冻结实现 | [aistra0528/Hail](https://github.com/aistra0528/Hail) | GPL-3.0 | 设计参考，未复制代码 |

## 未采用 / 合规排除来源

以下项目不作为本项目实现依据，不复制、不改写、不复用源码；仅作为历史调研记录保留，避免后续误用：

| 项目 | 风险原因 | 当前处理 |
|------|----------|----------|
| [hnpdsyn/PhoneGuard](https://github.com/hnpdsyn/PhoneGuard) | 仓库无 LICENSE 文件，无法确认复制、修改、再分发授权 | 已确认排除；已从参考实现清单移除，不得直接采用其实现 |
| [me.lucky/wasted](https://github.com/me.lucky/wasted) | 仓库当前无法访问（HTTP 404），许可证无法核实 | 已确认排除；已从参考实现清单移除，不得直接采用其实现 |

## 许可证文本位置

- 木兰宽松许可证第 2 版（MulanPSL-2.0）全文：见本项目根目录 [LICENSE](LICENSE)；APK 内置副本见 `assets/license/LICENSE-MulanPSL-2.0.txt`
- Apache-2.0 / MIT 全文：见本项目根目录 [NOTICE](NOTICE)（含各上游项目的完整版权信息与许可文本）
- release 运行时传递依赖逐项清单：见 [docs/DEPENDENCY_LICENSES.md](docs/DEPENDENCY_LICENSES.md)（`gradle generateDependencyLicenseReport` 可再生）

## 上游版权声明（按许可证要求保留）

各上游项目的版权归属、NOTICE 说明及完整许可文本见根目录 [NOTICE](NOTICE)，此处不再重复全文，仅保留义务摘要：

### GPL-3.0（Extinguish / Hail）

以上项目按 GNU 通用公共许可证第 3 版分发。本项目未复制或改写其代码，仅作能力设计参考，不构成其衍生作品；其版权与许可版权归原作者所有，完整 GPL-3.0 文本见各上游仓库。本项目保留此致谢。

### MIT（Fake-Lock-Screen）

完整 MIT 许可文本（含版权声明）见 [NOTICE](NOTICE)。

### Apache-2.0（Shizuku）

Shizuku 仓库无独立 NOTICE 文件；完整 Apache-2.0 许可文本见 [NOTICE](NOTICE)。

## 合规说明

1. 本项目主许可证为 **MulanPSL-2.0**（木兰宽松许可证第 2 版，见 [LICENSE](LICENSE)）。
2. 经审查（见 [docs/许可合规审查报告.md](docs/许可合规审查报告.md)），本项目为独立原创实现，不构成任何 GPL-3.0 上游项目的衍生作品，无 GPL 传染性分发义务。
3. `hnpdsyn/PhoneGuard` 仓库无 LICENSE 文件：本项目已确认排除，**不得复制、改写或直接复用**其代码。
4. `me.lucky/wasted` 仓库当前不可访问且许可证无法核实：本项目已确认排除，**不得复制、改写或直接复用**其代码。
5. 当前实现基于 Android SDK 公开 API、Shizuku 依赖和本项目原创代码；若后续发现任何来自未授权来源的复制实现，必须删除并以原创实现替换。
6. 历史版本说明：`deliverables/` 与 `apk/` 中以 GPL-3.0 发布的历史二进制与源码包，其 GPL-3.0 授权永久有效、不可撤销，其源码提供义务按当时条款继续履行；自本次换标起的新版本以 MulanPSL-2.0 授权。

---
本文件由 tiedun 项目维护，随项目分发。
