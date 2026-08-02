# Repository Guidelines

## 项目概览与技术栈

XinChat 是 Android 单 Activity、多模块通讯应用，使用 Jetpack Compose、MVVM 与单向数据流。

| 领域 | 技术与职责 |
| --- | --- |
| 语言与构建 | Kotlin、Gradle Kotlin DSL、Version Catalog、Convention Plugin |
| UI | Jetpack Compose、Material 3、Edge-to-Edge、`core:designsystem` |
| 架构与状态 | MVVM、UDF、AndroidX ViewModel、StateFlow、Coroutines |
| 依赖注入 | Hilt、KSP、构造函数注入 |
| 导航 | Navigation 3、类型安全路由键、单 Activity 返回栈 |
| 数据 | Repository、Retrofit、OkHttp、Kotlin Serialization、Room |
| 质量 | JUnit、Compose UI Test、Android Instrumentation Test、Lint |

当前仓库仍处于基础搭建阶段。已存在 `app`、`core:designsystem`、`core:ui`、`core:navigation`、`feature:auth`、`feature:chat`、`feature:user` 与 `build-logic`；未落地的 ViewModel、Repository、数据库、网络或导航实现不得假装已经存在。

## 执行原则与资料优先级

修改前先核对源码、调用方、构建脚本和测试，禁止根据旧项目、其他平台或记忆猜测 API。

资料优先级：

1. 用户当前任务与本文件。
2. 当前源码、`gradle/libs.versions.toml`、Convention Plugin、调用关系和测试。
3. 当前依赖版本的源码与 Android/Kotlin/Gradle 官方文档。

文档与代码不一致时，以可运行实现和验证结果为依据。新增依赖必须进入 Version Catalog；多个模块复用的 Gradle 配置优先进入 `build-logic`，不得复制过时示例或创造项目不存在的 API。

## 模块与依赖边界

- `app` 负责 Application/Activity、应用级主题、导航宿主和 Feature 装配，不承载业务实现。
- `feature:<name>` 负责独立业务能力及屏幕级 UI、ViewModel、UiState、导航入口和该 Feature 私有实现。
- `core:designsystem` 负责 Material 3 主题、颜色、排版、形状和设计 token，不依赖 `feature` 或 `app`。
- `core:ui` 只放跨 Feature 复用的无业务 UI、通用状态页和 Compose 工具。
- `core:navigation` 放跨模块导航契约、类型安全路由键及公共导航能力，不持有页面 UI。
- Feature 之间不得直接依赖或导入彼此内部实现。跨 Feature 跳转通过公开导航契约，由 `app` 组装。
- Core 不依赖 Feature 或 App。共享能力达到两个以上 Feature 的真实复用需求后再提升到新的 `core:*` 模块，并同步 `settings.gradle.kts`、Version Catalog 和 Convention Plugin。
- Domain 层按复杂度引入：只有业务规则被多个 ViewModel 复用或 ViewModel 明显过重时才创建 UseCase，不为形式完整制造空层。

## Feature 与 MVVM 结构

新页面优先按职责组织，而不是机械创建空文件：

```text
feature/<name>/src/main/java/<package>/
├── navigation/          # 公开路由键和 entry provider
├── ui/
│   ├── <Name>Route.kt   # ViewModel 获取、生命周期感知收集、导航协调
│   ├── <Name>Screen.kt  # 无状态 UI，仅接收 UiState 与回调
│   ├── <Name>UiState.kt
│   └── <Name>ViewModel.kt
├── data/                # 仅该 Feature 使用的数据实现
└── domain/              # 确有复用/复杂业务规则时创建
```

- `Route` 是屏幕入口：获取 `@HiltViewModel`，使用 `collectAsStateWithLifecycle()` 收集状态，再将纯数据和事件回调传给 `Screen`。
- `Screen` 和可复用 Composable 不直接获取 ViewModel、Repository、NavBackStack 或 DataSource，确保可预览和可测试。
- ViewModel 通过构造函数注入 Repository/UseCase，使用 `viewModelScope`，对外暴露不可变 `StateFlow<UiState>`。
- ViewModel 不保存 `Activity`、`Context`、`Resources`、Composable、NavController/NavBackStack 或 View 引用；需要平台能力时抽象到合适层。
- 使用 UDF：状态向下、事件向上。业务结果进入 UiState；导航、Snackbar 等 UI 行为优先由 UI 层根据明确状态处理，不默认创建易丢失的单次事件 Channel。
- 可复用组件使用普通 state holder 或状态提升，不在组件内部创建屏幕级 ViewModel。
- UI 元素私有状态留在最低公共 Composable，并按恢复需求选择 `remember` 或 `rememberSaveable`。

## 数据层规范

- UI 和 ViewModel 不直接访问 Retrofit Service、DAO、DataStore 或系统数据源，只依赖 Repository/UseCase。
- Repository 是数据层公开入口和单一事实来源；远端、数据库和缓存分别由明确命名的 DataSource/DAO/Service 承担。
- 使用 `suspend` 表示一次性操作，使用 `Flow` 表示持续数据；不要在数据层暴露 `MutableStateFlow` 给上层修改。
- DTO、Room Entity、领域模型和 UiModel 按语义分离并显式映射，禁止把网络可空性或数据库细节泄漏到 UI。
- 网络与数据库操作遵守结构化并发，Dispatcher 需可注入测试；不要吞异常或在 Repository 中直接显示 Toast/Snackbar。
- 共享数据放 `core:data` 等明确模块前，先确认存在跨 Feature 复用；仅单 Feature 使用时就近放置。
- 修改 Room、Hilt、KSP 或序列化代码后运行对应代码生成与编译任务，禁止手工修改生成代码。

## Compose、主题与资源

- 优先使用 Material 3 和 `core:designsystem` 的 `MaterialTheme`、语义颜色、Typography、Shape；禁止在业务 UI 重复定义品牌 token。
- Composable 参数保持稳定、明确：`modifier: Modifier = Modifier` 通常放在首个可选参数；状态与事件回调分离。
- 页面内容优先无状态化，列表提供稳定 key；昂贵派生值按需使用 `remember`/`derivedStateOf`，不要用它们掩盖错误状态归属。
- 正确处理 Edge-to-Edge、WindowInsets、系统栏、键盘、横竖屏和自适应宽度；不要依赖固定设备尺寸。
- 用户可见文案进入 Android string resources；颜色、尺寸、图标和无障碍说明使用正确资源或设计系统语义。
- 图标按钮、图片和自定义手势提供 contentDescription、语义、触摸目标和键盘/焦点支持。
- 不在组合期间执行网络、数据库写入或导航；副作用使用正确的 Effect API，并保持 key 稳定。

## Navigation 3

- 应用采用单 Activity；顶层 NavBackStack 由应用级导航宿主持有并保存恢复。
- 路由使用类型安全、可保存的 NavKey；参数保持最小，只传 ID 等稳定标识，不传大型对象、ViewModel 或 Repository。
- Feature 公开路由契约和 entry provider，内部 Screen 保持私有；`app` 聚合各 Feature entry。
- ViewModel 不直接操作返回栈。UI/navigation 层响应用户动作或已完成的业务状态，并执行导航。
- Deep Link、鉴权重定向和返回栈替换在应用导航层集中处理，避免每个 Screen 复制判断。
- 编写 Navigation 3 代码前核对当前版本 API 和现有实现，不套用 Navigation 2 的 `NavController` 示例。

## Preview 与测试

- 完整页面为无 ViewModel 的 `Screen` 提供 `@Preview`；用稳定假数据覆盖 Loading、Content、Empty、Error 等关键 UiState。
- Preview 不启动 Hilt、不访问网络/数据库、不依赖真实导航返回栈；必要时使用 PreviewParameterProvider。
- UI 变更至少检查浅色、深色、字体缩放和任务涉及的手机/平板尺寸。
- ViewModel 与 Repository 使用本地单元测试，优先 Fake 而不是过度 Mock；测试 StateFlow、成功、失败、空数据和并发边界。
- Compose 行为使用语义节点编写 UI 测试；导航关键路径编写回归测试。
- 测试文件与生产包路径对应，名称使用 `<Subject>Test.kt`。

## Kotlin、命名与注释

- 遵循 Kotlin 官方代码风格和项目 formatter。类型使用 `PascalCase`，函数与属性使用 `camelCase`，常量使用 `UPPER_SNAKE_CASE`，包名全小写。
- 文件名与主要类型一致，如 `ChatViewModel.kt`；避免 `Common.kt`、`Utils.kt`、`Manager.kt` 等无法体现领域职责的名称。
- UiState 使用 `<Screen>UiState`，ViewModel 使用 `<Screen>ViewModel`，Repository 接口使用领域名，默认实现可使用 `Default<Domain>Repository`。
- 注释说明业务含义、约束和设计原因，不复述代码，不保留“临时”“以后修改”等过程性描述。
- 公共 API 和非显然约束使用 KDoc；私有显然代码不机械补注释。

## Gradle、生成文件与依赖

- 依赖版本和别名统一维护在 `gradle/libs.versions.toml`。
- 多模块通用 Android、Compose、Hilt 和测试配置优先维护在 `build-logic/convention`。
- Feature 优先使用项目 Convention Plugin；若迁移现有模块，必须逐个验证变体、namespace、BuildConfig 和依赖变化。
- Kotlin/JVM target、compileSdk、minSdk 和 targetSdk 保持统一，除非平台要求有明确理由。
- 禁止手工修改 `build/`、KSP/Hilt/Room 生成目录和 APK/AAB 产物。
- 凭据通过环境变量、`local.properties` 或 CI Secret 提供，不写入源码、Version Catalog 或提交记录。

## Android 配置与发布

- 应用显示名、包名、namespace、applicationId、Manifest、主题和资源修改必须保持一致。
- 启动图标和 SplashScreen 使用 Android 资源及官方 API，检查 adaptive icon、monochrome icon 和 Android 12+ 启动页。
- Release 保持 R8、资源压缩和 consumer rules 可验证；签名配置不得提交密钥或密码。
- Manifest 权限最小化，并同步处理运行时权限、隐私说明、备份与数据提取规则。

## 验证与变更范围

- 只修改任务范围内文件，保留工作区已有改动，不顺带格式化或修复无关代码。
- 先运行最小相关测试，再按风险执行：

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
./gradlew lint
./gradlew assembleRelease
git diff --check
```

- UI/导航/权限等设备行为按需运行 instrumentation 或 Compose UI 测试。
- 报告实际执行的命令和结果；受网络、SDK、设备、签名或凭据阻塞时明确说明，不能把未执行验证描述为通过。
- 提交信息使用简短中文动宾结构并聚焦单一主题。PR 说明影响模块、验证命令、风险及必要截图。
