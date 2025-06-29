# Feature Registry Auto-Generation

The `FeatureRegistry.java` file is **automatically generated** from your actual Feature classes. This eliminates manual maintenance and ensures perfect consistency between your features and the config system.

## 🔄 Workflow

When you add or remove features:

1. **Add/modify features** in `ClientFeatureController` or `ServerFeatureController`
2. **Run**: `./gradlew updateFeatureRegistry`
3. **Run**: `./gradlew build` (to compile with the updated registry)
4. **Commit** both your feature changes AND the updated `FeatureRegistry.java`

## 📁 Generated Files

- `src/main/java/com/dementia/neurocraft/common/features/FeatureRegistry.java` - Auto-generated registry
- Contains `CLIENT_FEATURE_IDS` and `SERVER_FEATURE_IDS` lists

## ⚙️ Available Tasks

- `./gradlew generateFeatureRegistry` - Generate registry (requires compiled classes)
- `./gradlew updateFeatureRegistry` - Full workflow: compile → generate → clean for recompile

## ⚠️ Important Notes

- **DO NOT** manually edit `FeatureRegistry.java` - your changes will be overwritten
- The file contains a warning header indicating it's auto-generated
- Always run the update task when adding/removing features
- The registry ensures server configs work without depending on client-only classes

## 🎯 Benefits

- ✅ **Zero maintenance** - no manual ID lists to update
- ✅ **Perfect consistency** - impossible to have mismatched feature IDs
- ✅ **Build-time validation** - missing features cause compilation errors
- ✅ **Clean separation** - server configs don't import client classes 