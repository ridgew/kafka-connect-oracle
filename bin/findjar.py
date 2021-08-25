# -*- coding: utf-8 -*-
import os, re, sys
from os.path import join
from zipfile import ZipFile

def find_in_all_jars(matchstr, path):
    for root, dirs, files in os.walk(path):
        for name in files:
            if name[-4:] == '.jar' and handle_jar(matchstr, join(root, name)):
                print(join(root, name))

def handle_jar(matchstr, filename):
    isMatch = False
    with ZipFile(filename) as zipfile:
        for name in zipfile.namelist():
            if re.search(matchstr, name):
                print('\t', name)
                isMatch = True
    return isMatch


if len(sys.argv) != 2:
    print('''Usage: findjar ClassName.class''')
    sys.exit()

#find_in_all_jars(sys.argv[1], os.getcwd())  # 在当前目录及子目录中所有 jar 包中查
find_in_all_jars(sys.argv[1], '../libs/')