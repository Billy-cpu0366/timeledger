# 时间账本

个人自用的安卓时间记录 App。**说一句话 → AI 整理成带起止时间的记录 → 一天画成色块。**

基于 [Razeeman/Android-SimpleTimeTracker](https://github.com/Razeeman/Android-SimpleTimeTracker)（GPLv3）改造。

---

## 要解决的问题

市面上的时间记录 App 都要手动点点点，坚持不下去。这个项目把记录的摩擦降到最低：**开口说一句，剩下交给 AI。**

它记的是**全天时间去向**（吃饭、通勤、刷手机都算），不是工时。关心的不是「今天产出多少」，而是「我的时间到底去哪了」。

## 现状

原版已经能编译、能装、能跑。改造还没开始。

| 阶段 | 内容 | 状态 |
|---|---|---|
| 0 | 环境与前提验证 | 进行中 |
| 1 | 输入入口 + 原话落库 | 未开始 |
| 2 | AI 解析 | 未开始 |
| 3 | 色块视图 | 未开始 |
| 4 | 追问 + 待补全 | 未开始 |
| 5 | 真机自用一周 | 未开始 |

## 改造方案

### 六个模块

```
输入层 ──→ 记录服务 ──→ 存储
              │
              └──→ 解析器接口（DeepSeek 实现）

色块视图 ←── 存储（只读）
设置（API Key、分类）
```

### 四份契约

跨模块只传这四种东西：

1. **TextInput**：`(rawText: String, spokenAt: Long)` —— 输入层只交一个字符串，不管文字从哪来
2. **ParseResult**：`Parsed / NeedMore / Failed` 三选一的密封类
3. **状态推导**：不存状态，由时间字段推导
4. **分类**：复用上游的 `RecordType` 表，预置七项 + 提示词约束

### 关键决策

| 决策 | 理由 |
|---|---|
| **不调任何 ASR API** | 国产 ROM 实测无系统语音识别服务。改用输入法自带语音输入，App 不碰麦克风 |
| **只加不改** | 上游 16.8 万行、44 模块、活跃提交。改它的业务类会失去合并能力 |
| **用扩展表而非加字段** | `record_extras` 存原话，不动上游 `records` 表 |
| **待补全独立成表** | `incomplete_records`，避免 `timeEnded = 0` 破坏时长计算 |
| **自带 API Key，无后端** | 数据只在本机，只把文字发给 DeepSeek |

## 开发环境

| 项 | 版本 |
|---|---|
| JDK | 21 |
| Gradle / AGP | 8.10.1 / 8.10.1 |
| Kotlin | 1.9.25 |
| Room | 2.6.1 |
| minSdk / compileSdk | 24 / 36 |

## 构建

```bash
# 需要 local.properties 指向 SDK
echo "sdk.dir=/path/to/android-sdk" > local.properties

./gradlew assembleDebug --max-workers=1
```

产物：`app/build/outputs/apk/base/debug/app-base-debug.apk`

> `--max-workers=1` 是为了绕开某些网络环境下并发下载的 TLS 握手问题。

## 许可证

GPLv3。改造版分发时必须一并开源。
