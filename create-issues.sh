#!/bin/bash

# Script to create GitHub issues from markdown files in .github/issues/
# Usage: ./create-issues.sh

set -e

ISSUES_DIR=".github/issues"
REPO="DanielRTato/SiDi_Persistencia"

# Check if gh CLI is installed and authenticated
if ! command -v gh &> /dev/null; then
    echo "Error: GitHub CLI (gh) is not installed"
    echo "Install it from: https://cli.github.com/"
    exit 1
fi

if ! gh auth status &> /dev/null; then
    echo "Error: Not authenticated with GitHub CLI"
    echo "Run: gh auth login"
    exit 1
fi

# Function to extract value from frontmatter
extract_frontmatter_value() {
    local file="$1"
    local key="$2"
    grep "^${key}:" "$file" | sed "s/^${key}: *\"\(.*\)\"/\1/" | sed 's/"$//'
}

# Function to extract body (content after frontmatter)
extract_body() {
    local file="$1"
    # Extract everything after the second '---'
    awk '/^---$/{ if(++count==2) {p=1; next} } p' "$file"
}

# Process each markdown file in the issues directory
echo "Processing issue files in $ISSUES_DIR..."
echo ""

for issue_file in "$ISSUES_DIR"/*.md; do
    if [ ! -f "$issue_file" ]; then
        continue
    fi
    
    filename=$(basename "$issue_file")
    echo "Processing: $filename"
    
    # Extract metadata
    title=$(extract_frontmatter_value "$issue_file" "post_title")
    tags=$(extract_frontmatter_value "$issue_file" "tags")
    categories=$(extract_frontmatter_value "$issue_file" "categories")
    
    # Extract body
    body=$(extract_body "$issue_file")
    
    # Build labels from tags and categories
    labels=""
    if [ ! -z "$tags" ]; then
        # Convert tags array format to comma-separated
        labels=$(echo "$tags" | tr -d '[]"' | tr ',' '\n' | xargs)
    fi
    if [ ! -z "$categories" ]; then
        category_labels=$(echo "$categories" | tr -d '[]"' | tr ',' '\n' | xargs)
        if [ ! -z "$labels" ]; then
            labels="$labels,$category_labels"
        else
            labels="$category_labels"
        fi
    fi
    
    # Create the issue
    echo "Creating issue: $title"
    if [ ! -z "$labels" ]; then
        gh issue create \
            --repo "$REPO" \
            --title "$title" \
            --body "$body" \
            --label "$labels"
    else
        gh issue create \
            --repo "$REPO" \
            --title "$title" \
            --body "$body"
    fi
    
    if [ $? -eq 0 ]; then
        echo "✓ Successfully created issue: $title"
    else
        echo "✗ Failed to create issue: $title"
    fi
    echo ""
done

echo "All issues processed!"
