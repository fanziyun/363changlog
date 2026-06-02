# 363Changelog

一个 Minecraft Fabric 模组，用于在主菜单和世界选择界面展示整合包更新日志，支持远程获取与版本检测。

## 配置说明

配置文件由 Cloth Config 管理，可在游戏内 ModMenu → 363Changelog 中修改。

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `changelogUrl` | String | `""` | 远程 JSON 更新日志文件的 URL。 |
| `modpackVersion` | String | `"1.0.0"` | 当前整合包版本号，用于与远程最新版本对比。 |
| `showOnTitle` | Boolean | `true` | 是否在主菜单和暂停界面显示"更新日志"按钮。 |
| `enableVersionCheck` | Boolean | `true` | 是否启用自动版本检测，检测到新版本时显示提示。 |
| `versionYOffset` | Int | `20` | 主菜单版本文字（`v1.0.0`）的 Y 轴偏移量。 |
| `externalLinkName` | String | `"项目主页"` | 外部链接按钮的显示名称。 |
| `externalLinkUrl` | String | `"https://github.com/FanZiyun"` | 外部链接按钮的目标 URL。 |

### English

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `changelogUrl` | String | `""` | URL of the remote JSON changelog file. |
| `modpackVersion` | String | `"1.0.0"` | Current modpack version, used to compare with remote version for update checks. |
| `showOnTitle` | Boolean | `true` | Show the "Changelog" button on the title screen and pause screen. |
| `enableVersionCheck` | Boolean | `true` | Enable automatic version checking. Displays an indicator when a new version is available. |
| `versionYOffset` | Int | `20` | Y offset of the version text on the title screen. |
| `externalLinkName` | String | `"Homepage"` | Display name for the external link button. |
| `externalLinkUrl` | String | `"https://github.com/FanZiyun"` | Target URL for the external link button. |

---

## 更新日志 JSON 格式

### 顶层字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `footer` | String | 否 | 页脚文本（预留）。 |
| `tagColors` | Object | 否 | 自定义标签颜色映射，键为标签名，值为 `0xAARRGGBB` 或 `#RRGGBB` 格式的颜色字符串。 |
| `entries` | Array | **是** | 更新日志条目列表。 |

### Entry 字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `version` | String | **是** | 版本标识符（如 `"1.2.0"`）。 |
| `date` | String | 否 | 发布日期（格式自由，建议 ISO 8601）。 |
| `title` | String | 否 | 版本标题/名称。 |
| `type` | String[] | 否 | 更新类型标签。可选值：`major`（重大更新）、`minor`（功能更新）、`patch`（修复补丁）、`hotfix`（热修复）、`danger`（危险更新）。每种类型自带图标与颜色，不可自定义。 |
| `tags` | String[] | 否 | 自定义标签，与顶层 `tagColors` 配合使用可自定义颜色。 |
| `color` | String | 否 | 条目左侧竖条颜色，支持 `0xAARRGGBB`、`0xRRGGBB`、`#RRGGBB`、`#AARRGGBB` 格式。 |
| `changes` | String[] | **是** | 变更明细列表，每项为单条文本。 |

### English (Entry Fields)

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `version` | String | **Yes** | Version identifier. |
| `date` | String | No | Release date (ISO 8601 recommended). |
| `title` | String | No | Version title/name. |
| `type` | String[] | No | Update type tags: `major`, `minor`, `patch`, `hotfix`, `danger`. Each type has a fixed icon and color. |
| `tags` | String[] | No | Custom tags, used with top-level `tagColors` for custom styling. |
| `color` | String | No | Left color bar of the entry. Supports `0xAARRGGBB`, `0xRRGGBB`, `#RRGGBB`, `#AARRGGBB`. |
| `changes` | String[] | **Yes** | List of change details, each item is a single line of text. |

### 示例 / Example

```json
{
  "tagColors": {
    "优化": "0xFFFFAA00"
  },
  "entries": [
    {
      "version": "1.0.0",
      "date": "2026-06-01",
      "title": "初始版本",
      "type": ["major"],
      "tags": ["优化"],
      "color": "0xFF55FF55",
      "changes": ["更新日志系统已启用"]
    }
  ]
}
```

完整示例文件：[changelog.json](src/main/resources/changelog.json)

---

## 可视化编辑器

`changelog-editor/` 目录下提供了独立的 Web 端 JSON 编辑器，基于 Vue 3 + Vite 构建：

```bash
cd changelog-editor
npm install
npm run dev      # 启动开发服务器
npm run build    # 构建生产版本
```

---

## 开发 / Development

```bash
# Mod
./gradlew runClient    # 启动 Minecraft 开发实例
./gradlew build        # 编译 + 重混淆 JAR
./gradlew clean        # 清理构建产物

# Changelog 编辑器
cd changelog-editor && npm run dev
cd changelog-editor && npm run build
```