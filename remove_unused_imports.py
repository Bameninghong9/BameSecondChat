import os
import re

def process_java_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    lines = content.split('\n')
    new_lines = []
    
    # regex for import
    import_regex = re.compile(r'^import\s+(static\s+)?([\w\.]+)\.(\w+);')
    
    # Find all imported classes
    imports = []
    for i, line in enumerate(lines):
        match = import_regex.match(line.strip())
        if match:
            is_static = match.group(1) is not None
            pkg = match.group(2)
            cls = match.group(3)
            if cls != '*':
                imports.append((i, cls, line))

    # Strip imports from content to search in rest of file
    # We will search the whole content, but we must make sure the class name isn't just in the import line.
    
    lines_to_remove = set()
    for i, cls, line in imports:
        # Search for cls as a whole word in the content, but exclude the import line itself
        # A simple way: remove the line temporarily and search.
        temp_lines = lines.copy()
        temp_lines[i] = ""
        temp_content = '\n'.join(temp_lines)
        
        # Word boundary search for cls
        if not re.search(r'\b' + re.escape(cls) + r'\b', temp_content):
            print(f"Unused import found in {filepath}: {cls}")
            lines_to_remove.add(i)

    if lines_to_remove:
        for i, line in enumerate(lines):
            if i not in lines_to_remove:
                new_lines.append(line)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write('\n'.join(new_lines))
        print(f"Cleaned {filepath}")

def main():
    root_dir = r"C:\Users\thorb\.gemini\antigravity\scratch\BameSecondChat\src\main\java\com\bame\secondchat"
    for dirpath, _, filenames in os.walk(root_dir):
        for filename in filenames:
            if filename.endswith(".java"):
                process_java_file(os.path.join(dirpath, filename))

if __name__ == "__main__":
    main()
