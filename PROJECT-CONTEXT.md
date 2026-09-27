# 项目上下文

**模板版本：** 7.0

本文件位于目标工程根目录并由目标工程 Git 管理，只记录 AI Framework 无法推断的共享项目事实。`<agent-workspace>` 由 `./tools/ai-framework path` 定位；角色职责和流程规则以其中的 `agents/AGENTS.md` 与 `governance/` 为准，不在这里重复。

不得记录个人绝对路径、密码、token、个人偏好或仅对单台设备有效的配置。

## 项目

- **产品 / 服务：** bahar 主后端服务（会员 / 营销方向，待确认业务边界）
- **主要用户：** 待补充
- **仓库、模块与目录结构：** Maven 根 pom 位于**仓库顶层**（`pom.xml`），聚合 4 个模块：`bahar-utils`、`bahar-repository`、`bahar-framework`、`bahar-application`。`bahar-application` 为可启动模块，其余为基础模块。
- **现有架构与依赖方向：** Spring Boot 多模块单体；`bahar-application` 承载 Controller / 启动入口，基础能力下沉到 framework / repository / utils。精确依赖方向以各模块 pom 为准，不臆断。

## 工程命令

- **语言与框架：** Java 1.8（`java.version`=1.8，compiler source/target=1.8），Spring Boot，Maven 多模块
- **构建命令：** 在**仓库顶层**执行 `mvn clean compile`；打包 `mvn clean package`
- **快速测试命令：** `mvn -pl bahar-application test`（按需替换为受影响模块）
- **模块 / 集成测试命令：** `mvn test`（main + test 均已在 JDK 11 与 JDK 17 下实测通过）
- **Lint / 格式化命令：** 无统一配置，遵循 `governance/java-code-style.md`
- **本地开发前置条件：** JDK 1.8+（实测 11 / 17 均可编译，IDEA 项目 SDK 为 17）；Maven 及本地仓库已就绪

## Git 与交付

- **稳定 / 受保护分支：** `main`
- **日常集成分支：** `main`
- **Task 分支命名：** 建议 `task/<task-id>-<slug>`（待团队确认后固化）
- **Worktree 或等价隔离方式：** 遵循 `governance/git-worktree-governance.md`；`agent_bootstrap/` 只存在于主工作树，不得复制到 worktree
- **必需的 CI 检查：** 待补充
- **合并、发布与回滚流程：** 待补充

## 人工授权

- **授权记录方式：** 当前对话，或本地 DP / Task 中记录授权来源与范围
- **必须单独授权的操作：** 受保护分支操作、生产部署、数据迁移与删除；其余待补充

## 环境与部署

不存在的示例环境应删除；存在多个同类环境时分别使用唯一且稳定的 `Environment ID`。

| Environment ID | Type | Platform / Location | Purpose | Deployment Entry | Protection |
| --- | --- | --- | --- | --- | --- |
| `dev` | development | 待补充 | 开发联调 | 顶层 `mvn clean package` | 待补充 |
| `prod` | production | 待补充 | 正式运行 | 待补充 | 需要人工授权 |

- 部署 Task 必须引用具体的 `Environment ID`。
- 新增环境或改变环境保护规则时更新本节。
- 本节只记录长期稳定事实；单次部署目标、执行边界、授权、attempt 和结果记录在对应 Task。
- 生产部署、资源删除、数据迁移和权限扩大需要明确人工授权。

## 风险与敏感边界

- **认证 / 授权：** 后端登录含验证码接口（`BackendLoginCaptchaController`）；改动认证链路按 High 风险处理
- **数据库与迁移：** 根 `db/bahar-db.sql` 为库表脚本，变更需评估数据影响
- **外部 API / 队列 / 存储：** 待补充
- **不得读取或提交的敏感位置：** `bahar-application/src/main/resources/application.yaml` 含数据源等运行配置，不得提交真实凭据，不得外传
- **部署与运行环境限制：** 待补充

## 项目覆盖项

- **现有命名、代码或模块约定：** 模块统一 `bahar-` 前缀；groupId/artifactId 与 baharCar、baharCatering、baharHealth **完全相同**（`com.bahar:bahar`），因此**不可在同一 IDEA 项目内同时挂载多个工程的 pom**，否则模块名冲突
- **项目专属 Gate 或更严格规则：** 无
