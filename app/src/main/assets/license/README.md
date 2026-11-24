# tiedun 铁盾

Android 设备防护与应急处理工具：当设备被锁机程序或无障碍恶意锁屏控制时，通过物理按键组合快速呼出处理界面，强制全屏覆盖拦截，并借助 Shizuku / root 停止、停用或卸载问题组件。

## 许可

本项目按木兰宽松许可证第 2 版（Mulan PSL v2，SPDX: MulanPSL-2.0）发布，完整条款见随包 `LICENSE-MulanPSL-2.0.txt`（同仓库根目录 `LICENSE`）。

你可以使用、修改和再分发本软件，需：

- 保留版权、许可与免责声明；
- 修改后在改动文件中标注修改来源与日期；
- 不得以上游原作者名义推广衍生版本。

本项目源码公开发布；分发 APK 时本项目要求一并提供对应源码（含 SHA-256 对应标识）、`LICENSE-MulanPSL-2.0.txt`、`NOTICE` 与 `THIRD_PARTY_NOTICES.md`。

以 GPL-3.0 发布的历史版本（`deliverables/`、`apk/`）授权永久有效，不因换标撤销。

第三方项目清单与合规说明见 `THIRD_PARTY_NOTICES.md`，传递依赖许可清单见 `DEPENDENCY_LICENSES.md`。

## 上游参考

| 上游 | 许可证 | 关系 |
|------|--------|------|
| F111111shhh/Extinguish（fork 自 Moderpach/Extinguish） | GPL-3.0 | 设计参考：物理按键监听，未复制代码 |
| coderlidy/Fake-Lock-Screen | MIT | 设计参考：全屏覆盖，未复制代码 |
| RikkaApps/Shizuku | Apache-2.0 | 依赖引入：特权命令通道 |
| aistra0528/Hail | GPL-3.0 | 设计参考：停用/冻结，未复制代码 |
| hnpdsyn/PhoneGuard | 无 LICENSE | 已排除，不得复用 |
| me.lucky/wasted | 无法核实 | 已排除，不得复用 |

## 源码

源码与文档请从本项目仓库获取（https://github.com/lilining308-prog/tiedun ）。分发本 APK 时，请一并提供对应版本的完整源码、`LICENSE-MulanPSL-2.0.txt`、`NOTICE` 与 `THIRD_PARTY_NOTICES.md`。

完整条款与免责声明以 `LICENSE-MulanPSL-2.0.txt` 为准。
