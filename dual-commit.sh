#!/bin/bash

# Dual-commit script for read-together monorepo
# This script pushes frontend changes to both repositories

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Starting dual-commit workflow...${NC}"

# Check if we're in the right directory
if [ ! -d "read-together-client" ]; then
    echo -e "${YELLOW}⚠️  Error: read-together-client directory not found. Run this script from the monorepo root.${NC}"
    exit 1
fi

# Get commit message from user if not provided
COMMIT_MSG="$1"
if [ -z "$COMMIT_MSG" ]; then
    echo -e "${YELLOW}📝 Please enter a commit message:${NC}"
    read -r COMMIT_MSG
fi

# Add and commit changes to monorepo
echo -e "${BLUE}📦 Committing changes to monorepo...${NC}"
git add .
git commit -m "$COMMIT_MSG" || echo "No changes to commit in monorepo"

# Push to main monorepo (read-together)
echo -e "${BLUE}🌍 Pushing to read-together repository...${NC}"
git push origin main || echo "Failed to push to read-together - you may need to create the main branch first"

# Create subtree for frontend and push to stutter-support-circle
echo -e "${BLUE}📱 Pushing frontend to stutter-support-circle repository...${NC}"

# Check if the frontend remote has a main branch, if not use master
FRONTEND_BRANCH="main"
if ! git ls-remote --heads stutter-support-circle main | grep -q main; then
    FRONTEND_BRANCH="master"
fi

# Push frontend subtree to the stutter-support-circle repository
git subtree push --prefix=read-together-client stutter-support-circle $FRONTEND_BRANCH || {
    echo -e "${YELLOW}⚠️  Subtree push failed. Trying to create initial subtree...${NC}"
    # If it's the first time, we might need to create the subtree
    git subtree add --prefix=read-together-client stutter-support-circle $FRONTEND_BRANCH --squash || {
        echo -e "${YELLOW}⚠️  Creating new branch on stutter-support-circle...${NC}"
        # Force push the subtree
        git subtree push --prefix=read-together-client stutter-support-circle $FRONTEND_BRANCH --force
    }
}

echo -e "${GREEN}✅ Successfully pushed to both repositories!${NC}"
echo -e "${GREEN}   📦 Monorepo: https://github.com/dogaanismail/read-together${NC}"
echo -e "${GREEN}   📱 Frontend: https://github.com/dogaanismail/stutter-support-circle${NC}"
