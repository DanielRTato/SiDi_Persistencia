#!/usr/bin/env python3
"""
Script to create GitHub issues from markdown files in .github/issues/
This script requires the GitHub CLI (gh) to be installed and authenticated.
"""

import os
import re
import subprocess
import sys
from pathlib import Path

ISSUES_DIR = ".github/issues"
REPO = "DanielRTato/SiDi_Persistencia"


def check_gh_cli():
    """Check if gh CLI is installed and authenticated."""
    try:
        subprocess.run(["gh", "--version"], check=True, capture_output=True)
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("Error: GitHub CLI (gh) is not installed")
        print("Install it from: https://cli.github.com/")
        sys.exit(1)
    
    try:
        subprocess.run(["gh", "auth", "status"], check=True, capture_output=True, stderr=subprocess.STDOUT)
    except subprocess.CalledProcessError:
        print("Error: Not authenticated with GitHub CLI")
        print("Run: gh auth login")
        sys.exit(1)


def parse_frontmatter(content):
    """Parse YAML frontmatter from markdown content."""
    # Match content between --- markers
    pattern = r'^---\s*\n(.*?)\n---\s*\n(.*)$'
    match = re.match(pattern, content, re.DOTALL)
    
    if not match:
        return {}, content
    
    frontmatter_text = match.group(1)
    body = match.group(2)
    
    # Parse frontmatter fields
    frontmatter = {}
    for line in frontmatter_text.split('\n'):
        if ':' in line:
            key, value = line.split(':', 1)
            key = key.strip()
            value = value.strip().strip('"')
            frontmatter[key] = value
    
    return frontmatter, body


def parse_tags_list(tags_str):
    """Parse a tags list from frontmatter format like ["tag1","tag2"]."""
    if not tags_str:
        return []
    # Remove brackets and quotes, split by comma
    tags_str = tags_str.strip('[]')
    tags = [tag.strip().strip('"').strip("'") for tag in tags_str.split(',')]
    return [tag for tag in tags if tag]


def create_issue(filepath):
    """Create a GitHub issue from a markdown file."""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    frontmatter, body = parse_frontmatter(content)
    
    title = frontmatter.get('post_title', f"Issue from {filepath.name}")
    tags = parse_tags_list(frontmatter.get('tags', ''))
    categories = parse_tags_list(frontmatter.get('categories', ''))
    
    # Combine tags and categories
    labels = tags + categories
    
    print(f"Creating issue: {title}")
    
    # Build gh command
    cmd = ['gh', 'issue', 'create', '--repo', REPO, '--title', title, '--body', body]
    
    if labels:
        for label in labels:
            cmd.extend(['--label', label])
    
    try:
        result = subprocess.run(cmd, check=True, capture_output=True, text=True)
        issue_url = result.stdout.strip()
        print(f"✓ Successfully created: {issue_url}")
        return True
    except subprocess.CalledProcessError as e:
        print(f"✗ Failed to create issue: {title}")
        print(f"  Error: {e.stderr}")
        return False


def main():
    """Main function to process all issue files."""
    # Check prerequisites
    check_gh_cli()
    
    # Find all markdown files in issues directory
    issues_path = Path(ISSUES_DIR)
    if not issues_path.exists():
        print(f"Error: Directory {ISSUES_DIR} does not exist")
        sys.exit(1)
    
    issue_files = sorted(issues_path.glob("*.md"))
    
    if not issue_files:
        print(f"No markdown files found in {ISSUES_DIR}")
        sys.exit(0)
    
    print(f"Found {len(issue_files)} issue files to process")
    print()
    
    success_count = 0
    fail_count = 0
    
    for filepath in issue_files:
        if create_issue(filepath):
            success_count += 1
        else:
            fail_count += 1
        print()
    
    print(f"Summary: {success_count} issues created, {fail_count} failed")


if __name__ == "__main__":
    main()
