# Source Cleanup
Source cleanup for the applicable rules of the source.

# Rules
- Tab to space(4)
- Remove tailing space and tab
- Remove "\r" of "\r\n" in windows env.

# Install
- Downalod zip file in dist folder (**source-cleanup.zip**)
- Extract downloaded file and run **start.bat** or **start.sh** in the directory.

# Usage
Run command following and work all the sub-folders of the path

* Usages: start.bat -p \<absolute path\> [options]
  * -e,--extension <arg>   set file extension(default java)
  * -h,--help              print this help message
  * -p,--path <arg>        set absolute path denoting the starting directory

* Example: start.bat -p /home/user/targetRootFolder -e java

# Author
 ChangHyun Lee <leechwin1@gmail.com>

# License
This software is distributed in MIT license
