#!/usr/bin/env bash

# the following variables are provided externally (already exported)
echo "vcell_version='${vcell_version}', vcell_build='${vcell_build}'"
echo "installer_deploy_dir='${installer_deploy_dir}'"
echo "vcell_siteCamel='${vcell_siteCamel}'"

set -ux
	
	version=$(echo "${vcell_version}_${vcell_build}" | tr '.' _)

			pushd "${installer_deploy_dir}" || (echo "pushd ${installer_deploy_dir} failed"; exit 1)

			if ! rm "VCell_${vcell_siteCamel}_windows-x64_latest_64bit.exe" && \
			      ln -s "VCell_${vcell_siteCamel}_windows-x64_${version}_64bit.exe" \
				    "VCell_${vcell_siteCamel}_windows-x64_latest_64bit.exe";
			then echo "failed to create symbolic link for Win64 installer"; exit 1; fi

			if ! rm "VCell_${vcell_siteCamel}_unix_latest_32bit.sh" && \
			      ln -s "VCell_${vcell_siteCamel}_unix_${version}_32bit.sh" \
				    "VCell_${vcell_siteCamel}_unix_latest_32bit.sh";
			then echo "failed to create symbolic link for Linux32 installer"; exit 1; fi

			if ! rm "VCell_${vcell_siteCamel}_macos_latest_64bit.dmg" && \
			      ln -s "VCell_${vcell_siteCamel}_macos_${version}_64bit.dmg" \
				    "VCell_${vcell_siteCamel}_macos_latest_64bit.dmg";
			then echo "failed to create symbolic link for Macos installer"; exit 1; fi

			if ! rm "VCell_${vcell_siteCamel}_windows-x32_latest_32bit.exe" && \
			      ln -s "VCell_${vcell_siteCamel}_windows-x32_${version}_32bit.exe" \
				    "VCell_${vcell_siteCamel}_windows-x32_latest_32bit.exe";
			then echo "failed to create symbolic link for Win32 installer"; exit 1; fi

			if ! rm "VCell_${vcell_siteCamel}_unix_latest_64bit.sh" && \
			      ln -s "VCell_${vcell_siteCamel}_unix_${version}_64bit.sh" \
				    "VCell_${vcell_siteCamel}_unix_latest_64bit.sh";
			then echo "failed to create symbolic link for Linux64 installer"; exit 1; fi

			popd || (echo "pop failed"; exit 1)

echo "exited normally"

exit 0


