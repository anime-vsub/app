# Example Plugin Repository

This is an example repository structure for AnimeVsub plugins.

## Structure

```
example-repo/
├── index.json           # Repository manifest (required)
└── plugins/
    ├── com.example.anime/
    │   ├── icon.png
    │   └── com.example.anime-1.0.0.jar
    └── ...
```

## index.json Format

```json
{
  "repo": {
    "name": "Repository Name",
    "url": "https://your-domain.com/index.json",
    "icon": "https://your-domain.com/icon.png"
  },
  "plugins": [
    {
      "name": "Plugin Name",
      "packageName": "com.example.plugin",
      "version": "1.0.0",
      "versionCode": 10,
      "description": "Plugin description",
      "author": "Author Name",
      "icon": "https://example.com/icon.png",
      "url": "https://example.com/plugin.jar",
      "sha256": "sha256-hash-of-jar"
    }
  ]
}
```

## Building a Plugin

See `example-plugin/` for a complete plugin template.

### Requirements

- Kotlin 1.9+
- Target JVM 17
- Must implement `AnimeDataSource` interface

### Key Points

1. Plugin JAR must include all dependencies (fat JAR/shadow JAR)
2. Main class must have constructor with no args (for reflection loading)
3. Class must be named `{packageName}.DataSource`
4. JAR must be published with SHA256 checksum

### Build Command

```bash
cd example-plugin
./gradlew shadowJar
```

Output will be in `build/libs/example-plugin-1.0.0-all.jar`

## Hosting Your Repository

1. Create a GitHub Pages site or any static file hosting
2. Upload `index.json` to your root URL
3. Upload all plugin JARs
4. Update `index.json` with correct URLs

## Adding Repository in App

1. Go to Settings > Plugins
2. Tap the menu (three dots)
3. Select "Add Repository"
4. Enter your repository URL (e.g., `https://username.github.io/repo/index.json`)
5. Browse and install plugins