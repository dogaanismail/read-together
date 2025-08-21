# 🔄 Lovable + Monorepo Sync Workflow

## The Problem You Identified ✅
You're absolutely right! Without proper sync, changes made in Lovable could overwrite your local API integration work.

## Solution: Enhanced Bidirectional Sync 🛡️

### 🎯 How It Works

1. **Lovable → GitHub → Monorepo**: Lovable auto-commits to `stutter-support-circle` repo, script pulls changes
2. **Monorepo → GitHub → Lovable**: Your changes push to both repos, Lovable sees them automatically
3. **Conflict Prevention**: Smart detection of API files + automatic backups

### 🚀 Usage Commands

```bash
# Full sync workflow (recommended for most cases)
./dual-commit.sh "Add user authentication API integration"

# Pull latest Lovable changes only (before starting API work)
./dual-commit.sh pull

# Push your changes only (after API integration)
./dual-commit.sh push "Integrate room creation API"

# Full bidirectional sync (safest option)
./dual-commit.sh sync "Update room settings with backend API"

# Create backup before risky operations
./dual-commit.sh backup

# Show help
./dual-commit.sh help
```

### 🛠️ Recommended Workflow

**When Starting API Integration:**
1. `./dual-commit.sh pull` - Get latest Lovable changes
2. Work on API integration locally
3. `./dual-commit.sh push "API integration message"` - Push your changes

**When Lovable Makes UI Changes:**
1. `./dual-commit.sh pull` - Pull UI changes safely
2. Your API code is automatically preserved via git stash
3. Conflicts are detected and you're guided through resolution

**For Safe Full Sync:**
1. `./dual-commit.sh sync "message"` - Handles everything automatically

### 🔍 Smart Features

- **API Detection**: Automatically detects if you have API integration files
- **Auto Backup**: Creates timestamped backups before major operations  
- **Conflict Resolution**: Guides you through resolving any conflicts
- **Stash Management**: Preserves your work during pulls
- **Error Handling**: Clear messages and recovery instructions

### 📁 Protected API Directories

The script automatically protects these directories:
- `src/api/`
- `src/services/`
- `src/lib/api/`
- `src/hooks/api/`
- Any files with "*api*" in the name

### 🆘 If Conflicts Occur

The script will:
1. Create a backup automatically
2. Show you exactly what to do
3. Guide you through manual resolution
4. Let you retry safely

This ensures **no code is ever lost** and both Lovable and your API integration work seamlessly together!
