from setuptools import setup, find_packages

setup(
    name="vertexcache-sdk",
    version="0.1.0",
    packages=find_packages(where="python"),
    package_dir={"": "python"},
    install_requires=[],
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    python_requires=">=3.6",
)
