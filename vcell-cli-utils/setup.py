import re
from setuptools import setup
import subprocess
import sys
try:
    result = subprocess.run(
        [sys.executable, "-m", "pip", "show", "pkg_utils"],
        check=True, capture_output=True)
    match = re.search(r'\nVersion: (.*?)\n', result.stdout.decode(), re.DOTALL)
    assert match and tuple(match.group(1).split('.')) >= ('0', '0', '5')
except (subprocess.CalledProcessError, AssertionError):
    subprocess.run(
        [sys.executable, "-m", "pip", "install", "-U", "pkg_utils"],
        check=True)
import os
import pkg_utils

name = 'vcell_cli_utils'
dirname = os.path.join(os.path.dirname(__file__))
package_data = {
    name: [],
}

# get package metadata
md = pkg_utils.get_package_metadata(dirname, name, package_data_filename_patterns=package_data)

# install package
setup(
    name=name,
    version=md.version,
    description=(
        "Python utility package for VCell CLI Java App"
    ),
    long_description='README.rst',
    url="https://github.com/virtualcell/vcell_cli_utils",
    download_url='https://github.com/virtualcell/vcell_cli_utils',
    author='Virtual Cell',
    author_email="marupilla@uchc.edu",
    license="MIT",
    keywords=['HDF5', 'Visualization', 'YAML', 'Fire'],
    install_requires=md.install_requires,
    extras_require=md.extras_require,
    dependency_links=md.dependency_links,
    classifiers=[
        'Development Status :: 3 - Alpha',
        'Intended Audience :: Science/Research',
        'License :: OSI Approved :: MIT License',
        'Topic :: Scientific/Engineering :: Bio-Informatics',
    ],
)
