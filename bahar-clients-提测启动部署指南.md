# bahar 双端（收银端 / 会员端）完整性核查 + 提测阶段本地启动与部署指南

> 版本：v1.0　|　阶段：提测前置　|　范围：`baharCashier`（收银端）、`baharUniapp`（会员端）  
> 说明：本指南为**只读核查**结论，核查过程中未修改任何源码/配置、未执行 git 操作、未安装依赖。所有结论均附 `文件:行` 或命令输出证据。  
> **QA 独立复核（第二轮）**：已由 QA 不采信原结论、独立复算 `pages.json`（结论一致：83/83 命中）、复读 `.electron-vue/*` 与 `config/index.js`、复验依赖与产物，并做**反向核查**（见 §4.2 补充）。复核修正了 3 处路径拼写与 1 处 grep 计数表述。  
> 工作区根：`D:/git-double/bahar`（本指南**不涉及**后端四个 Java 工程）

---

## 目录

1. [结论先行（TL;DR）](#1-结论先行tldr)
2. [核查范围与方法](#2-核查范围与方法)
3. [收银端 baharCashier 完整性核查](#3-收银端-baharcashier-完整性核查)
4. [会员端 baharUniapp 完整性核查](#4-会员端-baharuniapp-完整性核查)
5. [三个疑点结论](#5-三个疑点结论)
6. [配置一致性核查](#6-配置一致性核查)
7. [缺失/异常项清单](#7-缺失异常项清单)
8. [启动指南](#8-启动指南)
9. [部署 / 打包指南](#9-部署--打包指南)
10. [提测前置 Checklist](#10-提测前置-checklist)
11. [附录：证据索引](#11-附录证据索引)

---

## 1. 结论先行（TL;DR）

| 项目                 | 是否完整   | 能否直接提测            | 关键结论                                                                                                                                     |
| ------------------ | ------ | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| 收银端 `baharCashier` | **完整** | ✅ 可（dev 模式）       | 依赖已装（763 个包，含 electron 22.3.27 二进制）；`dist/electron/main.js` 已构建（703 KB）。**唯一功能性隐患：打包脚本未传 `-m`，产物恒用生产域名，无法按 4 行业区分后端**。                   |
| 会员端 `baharUniapp`  | **完整** | ✅ 可（HBuilderX 运行） | `pages.json` 声明的 **83 个页面 100% 存在**；tabBar 4 页 + 8 图标齐全；uview-ui 1.8.3 / uni_modules 引用全部命中。**无 node_modules 属正常**（HBuilderX 编译，不需 npm）。 |

- **三个疑点均不阻断提测**：`TERGET_ENV` 拼写为「命名不一致但无功能影响」；根 `package.json` 为误覆盖、不影响 HBuilderX 编译；空目录 `baharUniapp/baharUniapp/` 无害、建议删除。
- **提测前必须处理 1 项高危**：收银端按行业打包时需补 `-m`（详见 §7、§9.1）。
- 缺失/异常项共 **10 条**，其中阻断 0 项、高 1 项、中 3 项、低 6 项。

---

## 2. 核查范围与方法

| 维度       | 方法                                                                                          | 证据形式            |
| -------- | ------------------------------------------------------------------------------------------- | --------------- |
| 页面/资源完整性 | 用 Node 脚本解析 `pages.json` 的 `pages` 与 `subPackages`，逐条 `fs.existsSync` 校验 `.vue` 与 tabBar 图标 | 脚本输出（@83/83 命中） |
| 依赖完整性    | 逐项读取 `node_modules/<pkg>/package.json` 的 `version`                                          | 版本列表输出          |
| 环境变量流向   | 阅读 `.electron-vue/*.js`、`config/index.js`、`env/*.env`，全库 `grep` 变量名                         | `文件:行`          |
| 入口/模板存在性 | `fs.existsSync` 批量校验关键文件                                                                    | 存在性输出           |
| 引用一致性    | `grep` `@/config`、`uview-ui`、`uni_modules` 等引用点                                             | `文件:行`          |

---

## 3. 收银端 `baharCashier` 完整性核查

### 3.1 技术栈与包管理器

| 项           | 值                                                                | 证据                                           |
| ----------- | ---------------------------------------------------------------- | -------------------------------------------- |
| 技术栈         | Electron 22.3.27 + Vue 2 (2.7.14) + electron-vue 脚手架 + Webpack 5 | `package.json:118`、`node_modules/vue@2.7.14` |
| 包管理器        | **yarn**（仅 `yarn.lock`，无 `package-lock.json`）                    | `ls` 输出：`yarn.lock`；`(no package-lock.json)` |
| 入口          | `main: ./dist/electron/main.js`                                  | `package.json:7`                             |
| 产品名 / appId | `bahar收银系统` / `cn.bahar.cashier`                                 | `package.json:36-37`                         |

> ⚠️ **必须用 yarn，不要用 npm install**（会因 lock 不一致 + `postinstall: electron-builder install-app-deps` 产生依赖树漂移）。

### 3.2 依赖安装情况（`node_modules`，共 763 项）

关键依赖版本核对（全部 ✅ 已安装）：

| 包                  | 声明       | 实装      | 状态 |
| ------------------ | -------- | ------- | -- |
| electron           | 22.3.27  | 22.3.27 | ✅  |
| webpack            | ^5.87.0  | 5.87.0  | ✅  |
| vue                | ^2.7.14  | 2.7.14  | ✅  |
| vue-loader         | 15.10.1  | 15.10.1 | ✅  |
| electron-builder   | ^24.4.0  | 24.13.3 | ✅  |
| esbuild-loader     | ^3.0.1   | 3.2.0   | ✅  |
| cross-env          | ^7.0.3   | 7.0.3   | ✅  |
| minimist           | ^1.2.8   | 1.2.8   | ✅  |
| dotenv             | ^16.1.4  | 16.1.4  | ✅  |
| @babel/core        | ^7.22.5  | 7.22.5  | ✅  |
| sass               | ^1.63.4  | 1.63.4  | ✅  |
| element-ui         | ^2.15.13 | 2.15.13 | ✅  |
| webpack-dev-server | ^4.15.1  | 4.15.1  | ✅  |
| portfinder         | ^1.0.32  | 1.0.32  | ✅  |

**Electron 运行时二进制已就绪**（dev 启动必需）：

```
OK  node_modules/electron/dist/electron.exe
node_modules/electron/path.txt -> electron.exe
```

### 3.3 构建产物与入口文件

| 路径                                                                            | 状态                          | 说明                                                     |
| ----------------------------------------------------------------------------- | --------------------------- | ------------------------------------------------------ |
| `dist/electron/main.js`                                                       | ✅ 存在，703,447 字节（2025-09-29） | Electron 主进程产物，`main` 指向它                              |
| `dist/electron/renderer.js`                                                   | ❌ 不存在                       | dev 下渲染进程由 dev-server 提供；打包时会重新生成，**不阻断**              |
| `dist/web/`                                                                   | ❌ 不存在                       | 尚未执行过 `build:web`                                      |
| `src/index.ejs`                                                               | ✅                           | HtmlWebpackPlugin 模板（`webpack.renderer.config.js:100`） |
| `src/renderer/main.js`、`App.vue`、`permission.js`、`error.js`、`router/index.js` | ✅                           | 渲染进程入口齐全                                               |
| `src/main/index.js`、`services/windowManager.js`                               | ✅                           | 主进程入口齐全                                                |
| `env/`、`build/`、`server/index.js`、`lib/updater.html`                          | ✅                           | 见下                                                     |

### 3.4 环境切换机制（核心）

**dev-runner 并不读取 `TERGET_ENV`。** 真正决定加载哪个 env 文件的是 `-m <mode>` 参数：

```js
// .electron-vue/utils.js:5-19
const argv = require('minimist')(process.argv.slice(2));
function getEnv() { return argv['m'] }
function getEnvPath() {
  if (typeof getEnv() === 'boolean' || typeof getEnv() === 'undefined')
    return rootResolve('env/.env');          // 无 -m 或无值 → env/.env
  return rootResolve(`env/${getEnv()}.env`); // 有 -m xxx → env/xxx.env
}
function getConfig() { return dotenv.config({ path: getEnvPath() }).parsed }
```

该 `getConfig()` 结果经 Webpack `DefinePlugin` 注入为 `process.env.userConfig`（`webpack.renderer.config.js:94-97`、`webpack.main.config.js:53-56`），供渲染层读取：

```js
// src/renderer/utils/request.js:6   —— 全部接口的 baseURL
baseURL: process.env.userConfig.API_HOST
// src/renderer/views/home/index.vue:30 / login/index.vue:59 / setting/index.vue:31
systemName: process.env.userConfig.SYSTEM_NAME
```

**模式 → 文件 → 后端 映射（已逐一核对）：**

| script             | `-m`     | 读取文件             | `API_HOST`（`env/*.env:1`）                 | SYSTEM_NAME    |
| ------------------ | -------- | ---------------- | ----------------------------------------- | -------------- |
| `dev` / `dev:shop` | `shop`   | `env/shop.env`   | `http://127.0.0.1:8081`                   | bahar收银系统-通用零售 |
| `dev:car`          | `car`    | `env/car.env`    | `http://127.0.0.1:8082`                   | bahar收银系统-汽车美容 |
| `dev:food`         | `food`   | `env/food.env`   | `http://127.0.0.1:8083`                   | bahar收银系统-餐饮   |
| `dev:health`       | `health` | `env/health.env` | `http://127.0.0.1:8084`                   | bahar收银系统-康养美业 |
| `dev:origin`       | 无        | `env/.env`       | `https://www.bahar.cn/bahar-application/` | bahar收银系统      |
| （无）                | 无        | `env/sit.env`    | `http://127.0.0.1:25565`                  | —（SIT 环境）      |

> `SYSTEM_NAME` 的界面体现：登录页、首页、设置页的标题/系统名（`views/login/index.vue:59`、`views/home/index.vue:30`、`views/setting/index.vue:31`）。切换行业时界面标题随 env 变化。

### 3.5 dev 运行时行为

- 渲染进程 dev-server 端口：`config/index.js:8` → `port: 8088`，`dev-runner.js:67` 以 8088 为 `basePort`，被占用则自动顺延。
- 主窗口加载地址（`src/main/config/StaticPath.js:15`）：
  ```js
  winURL = dev ? `http://localhost:${process.env.PORT}` : `file://${__dirname}/index.html`
  ```
  即 dev 下加载 dev-server，生产下加载本地 `index.html`。

---

## 4. 会员端 `baharUniapp` 完整性核查

### 4.1 技术栈

| 项             | 值                                      | 证据                                                                       |
| ------------- | -------------------------------------- | ------------------------------------------------------------------------ |
| 形态            | **HBuilderX 版 uni-app（Vue 2）**，源码平铺仓库根 | 无 `vite.config`/`vue.config`；`manifest.json` 无 `vueVersion` 字段 → 默认 Vue2 |
| UI 库          | uview-ui **1.8.3**（Vue2 版，手动安装布局）      | `uview-ui/package.json`                                                  |
| 是否需要 Node/npm | **否**。无 `node_modules`，由 HBuilderX 编译  | `ls` 无 `node_modules`、无 `unpackage/`                                     |

### 4.2 页面完整性（脚本校验：83/83 命中，0 缺失）

解析 `pages.json` 全部 `pages` + `subPackages` 后逐条校验 `.vue` 是否存在：

```
TOTAL DECLARED: 83  EXIST: 83  MISSING: 0
```

| 分组                            | 声明数    | 存在     | 缺失    |
| ----------------------------- | ------ | ------ | ----- |
| `pages/`（主包）                  | 44     | 44     | 0     |
| `subPackages → subPages`      | 14     | 14     | 0     |
| `subPackages → merchantPages` | 25     | 25     | 0     |
| **合计**                        | **83** | **83** | **0** |

tabBar（`pages.json:2-29`）四页 + 八图标全部存在：

```
OK  pages/index/index    OK  static/tabbar/home.png / home-active.png
OK  pages/category/index OK  static/tabbar/shop.png / shop-active.png
OK  pages/order/index    OK  static/tabbar/cate.png / cate-active.png
OK  pages/user/index     OK  static/tabbar/user.png / user-active.png
```

### 4.3 入口与依赖引用校验

`main.js` 的 import 全部可解析：

| import（`main.js`）   | 目标                                                  | 状态 |
| ------------------- | --------------------------------------------------- | -- |
| `./App`             | `App.vue`                                           | ✅  |
| `./store`           | `store/index.js`（另有 getters/mutation-types/modules） | ✅  |
| `uview-ui`          | `uview-ui/index.js`                                 | ✅  |
| `./core/bootstrap`  | `core/bootstrap.js`                                 | ✅  |
| `./utils/app`       | `utils/app.js`                                      | ✅  |
| `./core/ican-H5Api` | `core/ican-H5Api.js`                                | ✅  |

- uview 全局注册：`main.js:24` `Vue.use(uView)`；样式 `App.vue:62` `@import "uview-ui/index.scss";`、`uni.scss:82` `@import "uview-ui/theme.scss";`
- easycom 自动引入：`pages.json:573-578` `"^u-(.*)": "@/uview-ui/components/u-$1/u-$1.vue"`（`uview-ui/components/` 共 88 个组件，含 `u-popup`/`u-icon` 等，页面已实际使用）。
- `uni_modules`：`uni-popup`、`uni-row` 均存在，且已在页面实际引用（如 `subPages/coupon/detail.vue:48`、`subPages/timer/detail.vue:37`）。
- 自定义组件目录 `components/`：`jyf-parser`、`poster-img`、`mescroll-uni`、`neoceansoft-keyboard`（注意拼写为 neo**e**cansoft，非 neocansoft）等，齐全。

> **补充观察（QA 反向核查新增）**：以 `pages.json` 的 `pages`+`subPackages` 为基准反向扫描工程 `.vue`，发现 **约 34 个“页面形态”的 `.vue` 文件存在但未在 `pages.json` 注册**（另有约 13 个位于各页 `components/` 子目录的组件文件，属正常，不计）。未注册者集中在 `pages/coupon/*`、`pages/timer/*`、`pages/prestore/*`、`pages/book/*`、`pages/merchant/*`、`pages/commission/*`、`pages/comment/*`、`pages/empty.vue` 等 —— 与 `subPages/*`、`merchantPages/*` 中**已注册的同名页面并存，属历史迁移后的遗留重复（死文件），非“漏注册”**（已注册的分包版本才是生效页面）。**不影响编译与提测**，但建议后续清理，避免误改/误引用（例：孤立文件 `pages/coupon/list.vue:223` 仍 `$navTo('pages/timer/detail')` 指向未注册路径，仅因该文件本身未被注册才未触发）。

### 4.4 关键配置

| 项              | 值                                                                          | 证据                                           |
| -------------- | -------------------------------------------------------------------------- | -------------------------------------------- |
| 应用名 / appid    | `bahar会员营销系统` / `__UNI__958B06E`                                           | `manifest.json:2-3`                          |
| 版本             | versionName 2.0.0 / versionCode 200                                        | `manifest.json:5-6`                          |
| App 模块         | `app-plus.modules.Payment` 已启用                                             | `manifest.json:21-23`                        |
| 微信支付 appid     | `wxb6af3741686162bc`                                                       | `manifest.json:68`（App）、`:82`（小程序）           |
| H5             | title `bahar会员营销系统`，domain `www.bahar.cn`，`router.base=/h5/`，`mode=hash`   | `manifest.json:112-119`                      |
| uniCloud       | `_spaceID` `5ada423c-...`，`.hbuilderx/launch.json` 为 uniCloud remote       | `manifest.json:120`、`.hbuilderx/launch.json` |
| 接口配置           | `apiUrl: http://127.0.0.1:8081/`，`merchantNo: "10001"`，`name: bahar零售会员系统` | `config.js:3-8`                              |
| apiUrl 消费点     | `utils/request/index.js:10` `const baseURL = config.apiUrl;`               | ✅                                            |
| merchantNo 消费点 | `utils/request/index.js:74` 作为请求头 `merchantNo`（可被 storage 覆盖）              | ✅                                            |

---

## 5. 三个疑点结论

### 疑点 1 —— `TERGET_ENV` 拼写（疑似 `TARGET_ENV`）

**结论：命名不一致，但无任何功能影响，不阻断提测。**

- 全库 `grep` `TERGET_ENV|TARGET_ENV`（排除 `node_modules`）命中 **8 行 / 3 个文件**：`package.json:9-14`（6 个 dev 脚本的 `cross-env TERGET_ENV=development`）、`CHANGELOG.md:43`（历史说明）、以及**编译产物 `dist/electron/main.js:2005`（其中内嵌了 package.json 文本，非独立代码）**。
- **没有任何代码读取 `process.env.TERGET_ENV`**（亦无 `TARGET_ENV`）。
- 环境切换完全依赖 `-m <mode>` → `env/<mode>.env`（见 §3.4、`.electron-vue/utils.js:5-19`），与 `TERGET_ENV` 无关。
- `-m` 解析与映射已实测确认无歧义（shop→8081、car→8082、food→8083、health→8084）。
- **建议（可选、不影响提测）**：将脚本中 `TERGET_ENV` 统一为 `TARGET_ENV` 以消除歧义；或在 README 注明该变量为历史遗留、当前未生效。

### 疑点 2 —— 根 `package.json` 内容是 uni_module 清单

**结论：确属误覆盖，但不影响 HBuilderX 编译，不阻断提测。**

- 根 `package.json` 内容为：`"id": "neoceansoft-keyboard"`、名称「身份证/数字键盘/密码键盘/支付键盘」、`"version": "1.0.3"` —— 这是**组件插件清单**，而非工程清单。
- 工程内 `components/neoceansoft-keyboard/` 目录**恰好缺少自己的 `package.json`**（目录内仅 `neoceansoft-keyboard.vue`），高度符合「该组件的 package.json 被误拷贝/覆盖到工程根」的判断。
- HBuilderX 编译 uni-app 只读取 `manifest.json` + `pages.json`，**不读取根 `package.json`**，故编译不受影响；但它会误导（让人以为是 CLI 工程/触发 npm 支持）。
- **建议**：将工程根 `package.json` 替换为工程自身的（仅含 `name`/`version`/`private:true` 即可）或删除；并把该 uni_module 清单还原到 `components/neoceansoft-keyboard/package.json`。


### 疑点 3 —— 空目录 `baharUniapp/baharUniapp/`

**结论：空目录残留，无害，不阻断提测，建议删除。**

- 目录内容（`ls -la`）仅 `.` 与 `..`，**完全为空**，不含任何文件，更不含 `manifest.json`。
- HBuilderX 仅把「打开目录」所选的那一层（含 `manifest.json`）作为工程根；该空子目录不会被识别为工程或源码目录，**不会重复编译或报错**。
- 成因：疑似「另存为 / 复制 / 解压」产生的一次性残留。
- **建议**：直接删除 `baharUniapp/baharUniapp/`（仅此空目录）。

---

## 6. 配置一致性核查

### 6.1 收银端 env 端口 ↔ 后端四服务

| 行业   | env 文件             | `API_HOST`                                | 后端端口      | 一致性   |
| ---- | ------------------ | ----------------------------------------- | --------- | ----- |
| 通用零售 | `env/shop.env:1`   | `http://127.0.0.1:8081`                   | 8081      | ✅     |
| 汽车美容 | `env/car.env:1`    | `http://127.0.0.1:8082`                   | 8082      | ✅     |
| 餐饮   | `env/food.env:1`   | `http://127.0.0.1:8083`                   | 8083      | ✅     |
| 康养美业 | `env/health.env:1` | `http://127.0.0.1:8084`                   | 8084      | ✅     |
| 默认   | `env/.env:1`       | `https://www.bahar.cn/bahar-application/` | 生产域名      | 打包默认值 |
| SIT  | `env/sit.env:1`    | `http://127.0.0.1:25565`                  | —（**注意**） | ⚠️ 见下 |

> ⚠️ **SIT 端口需确认**：`env/sit.env` 指向 `127.0.0.1:25565`，而 `server/index.js:7` 的热更新静态服务恰好也监听 `25565`。二者端口相同，**请确认 SIT 环境后端是否真的在 25565**，以免误连到热更新服务。

### 6.2 会员端接口指向

- 会员端**只有一份 `config.js`**，`apiUrl` 当前指向零售 `8081`（`config.js:6`）。
- **按行业切换方式**：直接改 `config.js:6` 的 `apiUrl`（如汽车美容改 `http://127.0.0.1:8082/`），并同步 `config.js:3` 的 `name`。**当前无多环境/多行业配置文件机制**。
- 名称不一致：`config.js:3`「bahar零售会员系统」 vs `manifest.json:2`「bahar会员营销系统」（仅显示名差异，建议统一）。

---

## 7. 缺失/异常项清单

| #  | 项目  | 位置(文件:行)                                                                                                       | 现象                                                           | 影响                                                       | 严重级别  | 修复建议                                                             |
| -- | --- | -------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------ | -------------------------------------------------------- | ----- | ---------------------------------------------------------------- |
| 1  | 收银端 | `package.json:15-19` + `.electron-vue/build.js`（经 `webpack.*.config.js` → `.electron-vue/utils.js:11-19` 间接读取） | `build*` 脚本**未传 `-m`**，无 `-m` 时 `getEnvPath()` 回落 `env/.env` | **打包产物恒用生产域名**，无法按 4 行业区分后端                              | **高** | 打包时手动补 `-m`，或新增脚本（见 §9.1）                                        |
| 2  | 收银端 | `config/index.js:1-12` vs `hot-updater.js:44`                                                                  | 缺少 `build.hotPublishConfigName`                              | `pack:resources` 产出 `build/update/undefined.json`，热更新链断裂 | 中     | 在 `config/index.js` 的 `build` 下补 `hotPublishConfigName: 'bahar'` |
| 3  | 会员端 | `config.js:6`                                                                                                  | `apiUrl` 固定 8081                                             | 切行业需手改文件                                                 | 中     | 按需修改，或引入多行业配置文件                                                  |
| 4  | 会员端 | 根 `package.json`                                                                                               | 内容为 uni_module 清单                                            | 误导；不影响编译                                                 | 低     | 替换/删除（详见疑点 2）                                                    |
| 5  | 会员端 | `baharUniapp/baharUniapp/`                                                                                     | 空目录残留                                                        | 无                                                        | 低     | 删除该空目录                                                           |
| 6  | 收银端 | `package.json:9-14`                                                                                            | `TERGET_ENV` 拼写                                              | 无（无代码读取）                                                 | 低     | 可选统一为 `TARGET_ENV`                                               |
| 7  | 收银端 | `server/`（`server/index.js:5`）                                                                                 | 缺 `server/client` 静态目录                                       | `update:serve` 静态资源 404（仅热更新用）                           | 低     | 补齐或忽略                                                            |
| 8  | 收银端 | `dist/electron/`                                                                                               | 仅有 `main.js`，无 `renderer.js`                                 | dev 无影响；打包会重生成                                           | 低     | 无需处理                                                             |
| 9  | 收银端 | `env/sit.env:1`                                                                                                | 指向 `25565`（=热更新服务端口）                                         | SIT 可能误连                                                 | 低     | 确认并修正                                                            |
| 10 | 会员端 | `manifest.json:2` vs `config.js:3`                                                                             | 系统名不一致                                                       | 仅显示                                                      | 低     | 统一命名                                                             |

> 统计：阻断 **0**、高 **1**、中 **3**、低 **6**，合计 10 条。**仅第 1 条属提测/打包前必办事项；其余不阻断 dev 提测。**

---

## 8. 启动指南

### 8.1 收银端（IDEA / 命令行）

#### 前置环境

| 组件           | 要求                                                                           | 说明                                        |
| ------------ | ---------------------------------------------------------------------------- | ----------------------------------------- |
| Node.js      | 建议 16 / 18 LTS；实测本机 `v22.22.2` 亦可                                            | 依赖已装，无需重装                                 |
| yarn         | 必需（`npm i -g yarn`，或用 corepack 启用）                                           | **不要用 npm**                               |
| IDEA         | **Ultimate** 版（含 JavaScript/Node 运行配置）；Community 版无 Node 运行配置，请改用命令行/VS Code | 本工程非 Java 工程                              |
| Electron 二进制 | 已就绪                                                                          | `node_modules/electron/dist/electron.exe` |

#### 方式 A：IDEA 内置 Terminal（最简，推荐）

1. IDEA → `File → Open`，选择目录 **`D:\git-double\bahar\baharCashier`**（含 `package.json` 的那层；不要打开仓库根）。
2. 打开内置 `Terminal`（`Alt+F12`）。
   3. node -v
   4. npm -v
   5. npm install --global yarn
   6. yarn -v
3. 执行（首次可先装依赖，若 `node_modules` 已存在可跳过）：
   ```bash
   yarn cache clean
   yarn install          # 首次；切勿 npm install
   yarn dev:shop         # 启动「通用零售」行业（= dev）
   ```
4. 其他行业：
   ```bash
   yarn dev:car          # 汽车美容 → env/car.env → 8082
   yarn dev:food         # 餐饮      → env/food.env → 8083
   yarn dev:health       # 康养美业  → env/health.env → 8084
   yarn dev:origin       # 默认 env/.env（生产域名）
   ```
5. 预期：编译主/渲染进程 → 自动弹出 Electron 窗口并打开 DevTools；渲染 dev-server 从 `8088` 起（被占用自动顺延）。

#### 方式 B：IDEA「npm 运行配置」

`Run → Edit Configurations… → + → npm`，字段如下：

| 字段                    | 值                                                        |
| --------------------- | -------------------------------------------------------- |
| Name                  | `dev:shop`（自定义）                                          |
| package.json          | `D:\git-double\bahar\baharCashier\package.json`          |
| Command               | `run`                                                    |
| Scripts               | `dev:shop`                                               |
| Package manager       | `yarn`                                                   |
| Node interpreter      | `<你的 node.exe 路径>`（如 `C:\Program Files\nodejs\node.exe`） |
| Working directory     | `D:\git-double\bahar\baharCashier`（通常自动带入）               |
| Environment variables | 留空（env 由 `-m` 参数决定，无需此处设置）                               |

配置完成后点 ▶ 运行即可。切换行业：复制该配置，把 `Scripts` 改成 `dev:car` / `dev:food` / `dev:health` 即可。

#### 方式 C：External Tools（可选）

`Settings → Tools → External Tools → +`：

- Program：`yarn.cmd`（Windows）
- Arguments：`dev:shop`
- Working directory：`D:\git-double\bahar\baharCashier`

> **提示**：`dev` 与 `dev:shop` 完全等价（都带 `-m shop`）。

---

### 8.2 会员端（HBuilderX）

#### 前置环境

| 组件        | 要求                                             | 说明                           |
| --------- | ---------------------------------------------- | ---------------------------- |
| HBuilderX | **App 开发版**，建议 3.8.x / 3.99，或 4.x（均兼容 Vue2 工程） | 必须为「App 开发版」，标准版无法运行 uni-app |
| 微信开发者工具   | 运行/上传小程序时必需                                    | 需配置路径 + 开启服务端口               |
| Node.js   | **非必需**                                        | 本工程由 HBuilderX 编译，不用 npm     |

#### 导入工程

1. HBuilderX → `文件 → 打开目录`。
2. 选择 **`D:\git-double\bahar\baharUniapp`**（即**含 `manifest.json` 的那一层**）。
   - ⚠️ **不要**选到内层空目录 `baharUniapp\baharUniapp\`（见疑点 3）。

#### 运行 H5（浏览器预览）

`运行 → 运行到浏览器 → Chrome`。HBuilderX 会编译并以 H5 形式打开；`manifest.json:115-118` 的 `router.base=/h5/` 仅影响发行部署，本地运行由 HBuilderX 内置服务承载。

> 若接口跨域：本地后端需允许 CORS，或使用 HBuilderX 内置代理（本工程未见配置，默认直连 `config.js:6` 的 `apiUrl`）。

#### 运行微信小程序

1. 打开微信开发者工具 → `设置 → 安全设置 → 服务端口` → **打开**。
2. HBuilderX → `设置/工具 → 运行配置 → 小程序运行配置` → 填写**微信开发者工具安装路径**。
3. HBuilderX → `运行 → 运行到小程序模拟器 → 微信开发者工具`。
4. 首编译产物在 `unpackage/dist/dev/mp-weixin`，由微信开发者工具自动打开。

#### 运行到手机 / 模拟器（真机预览）

`运行 → 运行到手机或模拟器`：

- **Android**：开启手机 USB 调试后选择设备；首次会安装「标准基座」。
- **iOS**：需 Mac + 证书，或改用「运行到 iOS 模拟器」。
- 仅用于预览，不产出安装包（正式包走 §9.2 云打包）。

---

## 9. 部署 / 打包指南

### 9.1 收银端

#### （A）四行业打包命令与产物

由于 `build.js` 同样用 `-m` 选环境、而现有 `build*` 脚本**未传 `-m`**（见 §7 第 1 条），**推荐二选一**：

**做法 1（不改进脚本，两步手动执行，最稳妥）：**

```bash
# 通用零售（示例，按行业替换 shop/car/food/health）
npx cross-env BUILD_TARGET=clean node .electron-vue/build.js -m shop
npx electron-builder --win --x64            # 64 位；32 位用 --ia32
```

- 第一步：清理 + 编译主/渲染进程（读取 `env/shop.env`）。
- 第二步：electron-builder 出安装包。

**做法 2（推荐长期方案：给 package.json 增加带 `-m` 的专用脚本）：**

```jsonc
"build:win64:shop":   "cross-env BUILD_TARGET=clean node .electron-vue/build.js -m shop   && electron-builder --win --x64",
"build:win64:car":    "cross-env BUILD_TARGET=clean node .electron-vue/build.js -m car    && electron-builder --win --x64",
"build:win64:food":   "cross-env BUILD_TARGET=clean node .electron-vue/build.js -m food   && electron-builder --win --x64",
"build:win64:health": "cross-env BUILD_TARGET=clean node .electron-vue/build.js -m health && electron-builder --win --x64"
```

然后 `yarn build:win64:shop` 等。

**产物路径**（`package.json:38-40` `directories.output = "build"`）：

- 免安装目录：`build/win-unpacked/`
- NSIS 安装包：`build/bahar收银系统 Setup <version>.exe`（`win.target = nsis`，`package.json:62-65`）
- 更新元数据：`build/latest.yml`

#### （B）`build:win64` 与 `build:web` 的区别

| 命令            | 目标                                                                         | 产物                        | 用途                          |
| ------------- | -------------------------------------------------------------------------- | ------------------------- | --------------------------- |
| `build:win64` | `electron-builder --win --x64`                                             | Windows x64 安装包（`build/`） | 正式交付桌面客户端                   |
| `build:web`   | `BUILD_TARGET=web`→ Webpack 只打渲染进程（`webpack.renderer.config.js:3,123-126`） | 纯 Web 静态资源 `dist/web/`    | 用浏览器快速预览 UI，**不含 Electron** |

#### （C）`pack:resources` + `server/` 的用途（热更新）

- 流程：先 `build:dir` 产出 `build/win-unpacked/resources/app` → 再 `yarn pack:resources`（`hot-updater.js`）把 `dist/` 打成 `build/update/<hash>.zip` + `build/update/<config>.json`（含 version/hash）。
- `server/index.js`（`update:serve`）以 Express 在 `25565` 提供静态托管，供客户端 `electron-updater` 拉取增量包；`package.json:30-35` 的 `publish.url` 为 `http://127.0.0.1`。
- ⚠️ **依赖 `config.build.hotPublishConfigName`，但当前 `config/index.js` 未定义**（`hot-updater.js:44`），会产出 `undefined.json`。启用热更新前须先补该字段（见 §7 第 2 条）。

### 9.2 会员端

| 目标                   | HBuilderX 菜单                       | 产物路径                              | 说明                                                                                                      |
| -------------------- | ---------------------------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------- |
| **H5**               | `发行 → 网站-H5手机版`（或 网站-PC Web/手机 H5） | `unpackage/dist/build/h5/`        | 部署时须挂在 **`/h5/`** 路径（`router.base=/h5/`、hash 模式），域名 `www.bahar.cn`（`manifest.json:112-119`）             |
| **微信小程序**            | `发行 → 小程序-微信`                      | `unpackage/dist/build/mp-weixin/` | 用**微信开发者工具**打开该目录 → 「上传」。`appid=wxb6af3741686162bc`（`manifest.json:82`）须为你有权限的 appid，否则改用测试号            |
| **App（Android/iOS）** | `发行 → 原生App-云打包`                   | 云打包后返回下载链接（apk/ipa）               | 前置：登录 **DCloud 账号** + Android 证书（可用 DCloud 公共测试证书）/ iOS 证书与描述文件。已启用 `Payment` 模块（`manifest.json:21-23`） |

> 部署 H5 时注意：因走 hash 路由且 base 为 `/h5/`，Nginx 建议 `location /h5/ { try_files $uri $uri/ /h5/index.html; }`。

---

## 10. 提测前置 Checklist

- [ ] **后端 4 个 Java 服务**已启动，`8081 / 8082 / 8083 / 8084` **均连通可达**（`curl` 或浏览器访问健康接口）
- [ ] **MySQL** 就绪，库表结构与初始数据（含四行业）已导入
- [ ] 收银端启动脚本与目标行业匹配（`yarn dev:shop|car|food|health`），且 `env/*.env` 端口与后端一致
- [ ] 会员端 `config.js:6` 的 `apiUrl` 指向**正确行业**端口（默认零售 8081）
- [ ] **商户号 `10001`** 在后台商户列表中存在且有效（`config.js:8`、`utils/request/index.js:74`）
- [ ] 演示账号已准备：收银员账号、会员账号（手机号/验证码可收）
- [ ] 收银端：`node_modules` 完整（763 项）、`node_modules/electron/dist/electron.exe` 存在、`yarn -v` 可用
- [ ] 会员端：HBuilderX（App 开发版）已安装并能打开 `baharUniapp`
- [ ] 微信开发者工具已安装、**服务端口已开启**、HBuilderX「运行配置」已指向其安装路径
- [ ] 微信支付 / 小程序 **appid 权限**已确认（`wxb6af3741686162bc`）
- [ ] （打包前）收银端 `build*` 脚本补 `-m`；`config/index.js` 补 `hotPublishConfigName`（若启用热更新）
- [ ] （清理）删除空目录 `baharUniapp/baharUniapp/`；修正根 `package.json`（可选，不阻断）

---

## 11. 附录：证据索引

| 主题                 | 关键位置                                                                                                          |
| ------------------ | ------------------------------------------------------------------------------------------------------------- |
| 收银端脚本 / 依赖 / 入口    | `baharCashier/package.json:7,8-26,71-161`                                                                     |
| 模式→env 映射          | `baharCashier/.electron-vue/utils.js:5-19`                                                                    |
| env 注入             | `.electron-vue/webpack.renderer.config.js:94-97`、`.electron-vue/webpack.main.config.js:53-56`                 |
| 请求 baseURL         | `src/renderer/utils/request.js:6`                                                                             |
| 系统名展示              | `src/renderer/views/login/index.vue:59`、`views/home/index.vue:30`、`views/setting/index.vue:31`                |
| dev 引导 / 端口        | `.electron-vue/dev-runner.js:64-98,134-154`、`config/index.js:8`                                               |
| dev/prod 加载地址      | `src/main/config/StaticPath.js:15-16`                                                                         |
| 打包流程               | `.electron-vue/build.js:2,18-25,91-104`                                                                       |
| 打包输出/发布            | `package.json:27-70`                                                                                          |
| 热更新                | `.electron-vue/hot-updater.js:44-53,55-104`、`server/index.js:5-12`                                            |
| env 文件             | `env/.env:1-3`、`env/shop.env:1-3`、`env/car.env:1-3`、`env/food.env:1-3`、`env/health.env:1-3`、`env/sit.env:1-2` |
| TERGET_ENV 出现点     | `package.json:9-14`、`CHANGELOG.md:43`、`dist/electron/main.js:2005`（编译产物内嵌，非源码逻辑）                              |
| 会员端页面声明            | `baharUniapp/pages.json:2-29(tabBar),30-305(pages),308-556(subPackages),573-578(easycom)`                     |
| 会员端配置              | `manifest.json:2-6,21-23,68,82,112-120`、`config.js:3-8`                                                       |
| 会员端入口引用            | `main.js:2-14`、`App.vue:60-68`、`uni.scss:82`、`utils/request/index.js:10-11,74`                                |
| 根 package.json 误覆盖 | `baharUniapp/package.json:1-9`、`components/neoceansoft-keyboard/`（无 package.json）                             |

---

*本指南由工程师寇豆码基于只读核查产出；所有数据均可依上表 `文件:行` 复核。*
