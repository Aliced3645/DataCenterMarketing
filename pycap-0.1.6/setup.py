from distutils.core import setup, Extension
from distutils.util import get_platform
import sys, os, glob


# Generate HTML documentation from the ReST files
# if we find docutils lying around

try:
    import docutils.core
except:
    pass
else:
    for f in map(os.path.abspath, ['docs/README.txt', 'docs/TODO.txt', 'docs/DOCS.txt', 'docs/CHANGES.txt']):
        out = os.path.splitext(f)[0] + '.html'
        if (os.path.exists(out) and os.stat(out).st_mtime < os.stat(f).st_mtime) or (not os.path.exists(out)):
            print 'Generating HTML from %s' % (os.path.split(f)[-1], )
            docutils.core.publish_file(source_path=f,
                                       destination_path=out,
                                       reader_name='standalone',
                                       parser_name='restructuredtext',
                                       writer_name='html')

# All header files for the package
HEADERS = reduce(lambda x, y: x + y, map(glob.glob, ['src/protocol/*.h', 'src/inject/*.h', 'src/capture/*.h']))

# Source files for each module
CAPTURE_MODULE = glob.glob('src/capture/*.c')
INJECT_MODULE = glob.glob('src/inject/*.c')
PROTOCOL_MODULE = glob.glob('src/protocol/*.c')


# If any of the headers are newer than any of the sources
# we update the modified time on all the sources to force
# a recompile

HEADER_MTIMES = map(lambda x: os.stat(x).st_mtime, HEADERS)
SOURCE_MTIMES = map(lambda x: os.stat(x).st_mtime, CAPTURE_MODULE + PROTOCOL_MODULE + INJECT_MODULE)
if max(HEADER_MTIMES) > max(SOURCE_MTIMES):
    print "Headers are newer than source, updating modified times on source to force compilation"
    for f in CAPTURE_MODULE + PROTOCOL_MODULE + INJECT_MODULE:
        os.utime(f, None)

# XXX: What to do for Windows?
CFLAGS = []
LDFLAGS = []
if os.name == 'posix':
    CFLAGS = os.popen('libnet-config --cflags').read().split()
    CFLAGS += os.popen('libnet-config --defines').read().split()
    LDFLAGS = os.popen('libnet-config --libs').read().split()

setup(name="pycap",
      version="0.1.6",
      description="Python libpcap wrapper",
      author="Mark Rowe",
      url="http://pycap.sourceforge.net/",
      license='BSD',
      author_email="bdash@clear.net.nz",
      package_dir = {'pycap': 'src'},
      packages = ['pycap', 'pycap.constants'],
      ext_modules=[Extension('pycap.protocol', PROTOCOL_MODULE,
                             extra_compile_args=CFLAGS,
                             extra_link_args=LDFLAGS),
                   Extension("pycap.capture", CAPTURE_MODULE,
                             libraries=['pcap'],
                             extra_compile_args=CFLAGS,
                             extra_link_args=LDFLAGS),
                   Extension("pycap.inject", INJECT_MODULE,
                             libraries=['net'],
                             extra_compile_args=CFLAGS,
                             extra_link_args=LDFLAGS)]
      )
