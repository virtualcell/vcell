namespace java org.vcell.imagedataset

typedef i16 short
typedef i32 int
typedef i64 long

struct ISize {
	1: required int x;
	2: required int y;
	3: required int z;
}

struct Extent {
	1: required double x;
	2: required double y;
	3: required double z;
}

struct Origin {
	1: required double x;
	2: required double y;
	3: required double z;
}

struct ImageSizeInfo {
	1: required string imagePath;
	2: required ISize iSize;
	3: required int numChannels;
	4: required list<double> timePoints;
	5: required int selectedTimeIndex;
}
	
struct UShortImage {
	1: required list<short> pixels;
	2: required ISize size;
	3: required Extent extent;
	4: required Origin origin;
	5: optional string name;
	6: optional string description;
}

struct ImageDataset {
	1: required list<UShortImage> images;
	2: required list<double> imageTimeStamps;
	3: required int numZ;
}

typedef list<ImageDataset> ImageDatasetList;
typedef string FilePath;
typedef list<FilePath> FilePathList;

exception ThriftImageException {
   1: required string message;
}

service ImageDatasetService {
	ImageSizeInfo getImageSizeInfo(1:string fileName, 2:int forceZSize) 
				throws (1: ThriftImageException imageException);

	ImageDataset readImageDataset(1:string imageID) 
				throws (1: ThriftImageException imageException);

	ImageDatasetList readImageDatasetChannels(1:string imageID, 2:bool bMergeChannels, 3:int timeIndex, 4:ISize resize) 
				throws (1: ThriftImageException imageException);

	ImageDataset readImageDatasetFromMultiFiles(1:FilePathList files, 2:bool isTimeSeries, 3:double timeInterval)
				throws (1: ThriftImageException imageException);
}
