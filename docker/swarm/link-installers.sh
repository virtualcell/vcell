#!/usr/bin/env bash

shopt -s -o nounset
	
	version=$(echo "${vcell_version}_${vcell_build}" | tr '.' _)

			pushd ${installer_deploy_dir}

			rm VCell_${vcell_siteCamel}_windows-x64_latest_64bit.exe && \
			ln -s VCell_${vcell_siteCamel}_windows-x64_${version}_64bit.exe \
				  VCell_${vcell_siteCamel}_windows-x64_latest_64bit.exe
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Win64 installer"; exit 1; fi

			rm VCell_${vcell_siteCamel}_unix_latest_32bit.sh && \
			ln -s VCell_${vcell_siteCamel}_unix_${version}_32bit.sh \
				  VCell_${vcell_siteCamel}_unix_latest_32bit.sh
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Linux32 installer"; exit 1; fi

			rm VCell_${vcell_siteCamel}_macos_latest_64bit.dmg && \
			ln -s VCell_${vcell_siteCamel}_macos_${version}_64bit.dmg \
				  VCell_${vcell_siteCamel}_macos_latest_64bit.dmg
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Macos installer"; exit 1; fi

			rm VCell_${vcell_siteCamel}_windows_latest_32bit.exe && \
			ln -s VCell_${vcell_siteCamel}_windows_${version}_32bit.exe \
				  VCell_${vcell_siteCamel}_windows_latest_32bit.exe
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Win32 installer"; exit 1; fi

			rm VCell_${vcell_siteCamel}_unix_latest_64bit.sh && \
			ln -s VCell_${vcell_siteCamel}_unix_${version}_64bit.sh \
				  VCell_${vcell_siteCamel}_unix_latest_64bit.sh
			if [[ $? -ne 0 ]]; then echo "failed to create symbolic link for Linux64 installer"; exit 1; fi

			popd

echo "exited normally"

exit 0


