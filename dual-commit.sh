#!/bin/bash

# Enhanced dual-commit script for read-together monorepo
# This script handles bidirectional sync with Lovable and prevents conflicts

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
LOVABLE_PROJECT_PATH=""  # Set this to your Lovable project path if using local sync
FRONTEND_REMOTE="stutter-support-circle"
MAIN_REMOTE="origin"
FRONTEND_BRANCH="main"

echo -e "${BLUE}🔄 Starting enhanced dual-commit workflow...${NC}"

# Check if we're in the right directory
if [ ! -d "read-together-client" ]; then
    echo -e "${RED}❌ Error: read-together-client directory not found. Run this script from the monorepo root.${NC}"
    exit 1
fi

# Function to pull latest changes from frontend repo (Lovable updates)
pull_from_lovable() {
    echo -e "${BLUE}📥 Pulling latest changes from frontend repository (Lovable updates)...${NC}"

    # Stash any local changes first
    if ! git diff --quiet; then
        echo -e "${YELLOW}💾 Stashing local changes...${NC}"
        git stash push -m "Auto-stash before Lovable sync $(date)"
    fi

    # Pull latest changes from frontend repo
    git subtree pull --prefix=read-together-client $FRONTEND_REMOTE $FRONTEND_BRANCH --squash || {
        echo -e "${YELLOW}⚠️  No new changes from Lovable repository or conflict detected${NC}"
        # If there's a conflict, provide guidance
        if [ $? -ne 0 ]; then
            echo -e "${RED}🔍 Merge conflict detected! Please resolve manually:${NC}"
            echo -e "${YELLOW}   1. Resolve conflicts in read-together-client/ directory${NC}"
            echo -e "${YELLOW}   2. Run: git add read-together-client/${NC}"
            echo -e "${YELLOW}   3. Run: git commit -m 'Resolve Lovable sync conflicts'${NC}"
            echo -e "${YELLOW}   4. Re-run this script${NC}"
            exit 1
        fi
    }

    # Restore stashed changes if any
    if git stash list | grep -q "Auto-stash before Lovable sync"; then
        echo -e "${BLUE}📤 Restoring your local changes...${NC}"
        git stash pop || {
            echo -e "${RED}🔍 Conflict while restoring local changes! Please resolve manually${NC}"
            exit 1
        }
    fi
}

# Function to create a backup before major operations
create_backup() {
    BACKUP_DIR="./backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$BACKUP_DIR"
    cp -r read-together-client "$BACKUP_DIR/"
    echo -e "${GREEN}💾 Backup created at: $BACKUP_DIR${NC}"
}

# Function to check for API integration files
check_api_integration() {
    echo -e "${BLUE}🔍 Checking for API integration files...${NC}"

    # List of patterns that indicate API integration
    API_PATTERNS=(
        "read-together-client/src/api/"
        "read-together-client/src/services/"
        "read-together-client/src/lib/api"
        "read-together-client/src/hooks/api"
    )

    for pattern in "${API_PATTERNS[@]}"; do
        if [ -d "$pattern" ] || find read-together-client/src -name "*api*" -type f | grep -q .; then
            echo -e "${YELLOW}⚠️  API integration files detected. Extra caution needed.${NC}"
            return 0
        fi
    done

    return 1
}

# Main sync workflow
sync_workflow() {
    # Get commit message from user if not provided
    COMMIT_MSG="$1"
    if [ -z "$COMMIT_MSG" ]; then
        echo -e "${YELLOW}📝 Please enter a commit message:${NC}"
        read -r COMMIT_MSG
    fi

    # Create backup
    create_backup

    # Check for API integration
    if check_api_integration; then
        echo -e "${YELLOW}🤔 Would you like to pull latest Lovable changes first? (y/N)${NC}"
        read -r PULL_FIRST
        if [[ $PULL_FIRST =~ ^[Yy]$ ]]; then
            pull_from_lovable
        fi
    else
        # If no API integration detected, always pull first
        pull_from_lovable
    fi

    # Add and commit changes to monorepo
    echo -e "${BLUE}📦 Committing changes to monorepo...${NC}"
    git add .

    # Check if there are changes to commit
    if git diff --staged --quiet; then
        echo -e "${YELLOW}ℹ️  No changes to commit${NC}"
    else
        git commit -m "$COMMIT_MSG"
        echo -e "${GREEN}✅ Changes committed to monorepo${NC}"
    fi

    # Push to main monorepo (read-together)
    echo -e "${BLUE}🌍 Pushing to read-together repository...${NC}"
    git push $MAIN_REMOTE main || {
        echo -e "${RED}❌ Failed to push to read-together repository${NC}"
        exit 1
    }

    # Push frontend subtree to stutter-support-circle (this syncs to Lovable)
    echo -e "${BLUE}📱 Pushing frontend to stutter-support-circle repository (Lovable sync)...${NC}"
    git subtree push --prefix=read-together-client $FRONTEND_REMOTE $FRONTEND_BRANCH || {
        echo -e "${YELLOW}⚠️  Subtree push failed. Trying alternative method...${NC}"

        # Alternative: force push the subtree
        git subtree push --prefix=read-together-client $FRONTEND_REMOTE $FRONTEND_BRANCH --force || {
            echo -e "${RED}❌ Failed to push to frontend repository${NC}"
            exit 1
        }
    }

    echo -e "${GREEN}✅ Successfully synced to both repositories!${NC}"
    echo -e "${GREEN}   📦 Monorepo: https://github.com/dogaanismail/read-together${NC}"
    echo -e "${GREEN}   📱 Frontend: https://github.com/dogaanismail/stutter-support-circle${NC}"
    echo -e "${GREEN}   🎨 Lovable will pick up changes automatically${NC}"
}

# Command line options
case "$1" in
    "pull")
        echo -e "${BLUE}📥 Pulling latest changes from Lovable...${NC}"
        create_backup
        pull_from_lovable
        echo -e "${GREEN}✅ Pull completed!${NC}"
        ;;
    "push")
        echo -e "${BLUE}📤 Pushing local changes to repositories...${NC}"
        sync_workflow "$2"
        ;;
    "sync")
        echo -e "${BLUE}🔄 Full bidirectional sync...${NC}"
        sync_workflow "$2"
        ;;
    "backup")
        create_backup
        echo -e "${GREEN}✅ Backup completed!${NC}"
        ;;
    "help"|"-h"|"--help")
        echo -e "${BLUE}Enhanced Dual-Commit Script Usage:${NC}"
        echo -e "  ${GREEN}./dual-commit.sh${NC}                     - Full sync workflow (default)"
        echo -e "  ${GREEN}./dual-commit.sh 'commit message'${NC}    - Full sync with commit message"
        echo -e "  ${GREEN}./dual-commit.sh pull${NC}               - Pull latest from Lovable only"
        echo -e "  ${GREEN}./dual-commit.sh push 'message'${NC}     - Push local changes only"
        echo -e "  ${GREEN}./dual-commit.sh sync 'message'${NC}     - Full bidirectional sync"
        echo -e "  ${GREEN}./dual-commit.sh backup${NC}             - Create backup only"
        echo -e "  ${GREEN}./dual-commit.sh help${NC}               - Show this help"
        ;;
    *)
        sync_workflow "$1"
        ;;
esac
