#!/usr/bin/env python3
import argparse
import os
import sys
import ntpath
import subprocess
import shutil
from glob import glob

parser = argparse.ArgumentParser(description='Build and move mod\'s JAR file')
parser.add_argument('--path', help='Where to copy the JAR output file', default='~/Library/Application Support/minecraft/mods')
args = parser.parse_args()
OUTPUT_PATH = os.path.expanduser(args.path)

def get_build_path():
    ''' Return the path to the JAR file that was output '''
    files = glob('./build/libs/dronez*.jar')
    if len(files) == 0:
        print('ERROR: NO FILES FOUND IN ./build/libs')
        sys.exit(1)
    elif len(files) > 1:
        print('ERROR: MORE THAN ONE JAR FILE FOUND IN ./build/libs')
        sys.exit(1)
    
    return [files[0], ntpath.basename(files[0])]


# Check if we're running on Windows. We're not ready for this yet.
if os.name == 'nt':
    print('ERROR: This tool does not yet support Windows :(')
    sys.exit(1)

print('\n== Building ==')
subprocess.call(['./gradlew', 'build'])

print('\n== Moving output JAR ==')
jar_file_path, jar_file_name = get_build_path()
OUTPUT_PATH = os.path.join(OUTPUT_PATH, jar_file_name)
print(f'Source: {jar_file_path}')
print(f'Destination: {OUTPUT_PATH}')
shutil.move(jar_file_path, OUTPUT_PATH)

print('\n== Opening Minecraft ==')
subprocess.call(['open', '/Applications/Minecraft.app'])

print('Done :)\n')