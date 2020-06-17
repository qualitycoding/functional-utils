//package me.shaftesbury.utils.functional;
//
//import io.vavr.collection.List;
//import io.vavr.collection.Set;
//
//// This should be, at the very least, a new minor version, eg 1.1.0-SNAPSHOT
//public interface Listable {
//    List<String> getAllKeys();
//}
//
//public interface Writable {
//    void write(final Object o);
//}
//
//public interface Readable /* it is possible that this interface should extend Listable */ {
//    Object read(final String key);
//}
//
//public interface Deletable {
//    void delete(final String key);
//}
//
//@Deprecated
//public interface ObjectStore extends Listable, Readable, Writable, Deletable {
//
//}
//
//@Deprecated
//public class S3ObjectStore implements ObjectStore {
//
//}
//
//@Deprecated
//public class FileSystemObjectStore implements ObjectStore {
//
//}
//
//public class S3ReadableObjStore implements Readable {
//
//}
//
//public class S3WritableObjStore implements Writable {
//
//}
//
//public class S3ListableObjStore implements Listable {
//
//}
//
//public class S3DeletableObjStore implements Deletable {
//
//}
//
//public class FSReadableObjStore implements Readable {
//
//}
//
//public class FSWritableObjStore implements Writable {
//
//}
//
//public class FSListableObjStore implements Listable {
//
//}
//
//public class FSDeletableObjStore implements Deletable {
//
//}
//
//// A multi-bucket store might be initialised like this
//// but maybe it's not actually needed because we only ever read or write to a store in a given code path
//public class MultiBucketS3ObjectStore implements ObjectStore {
//    private final Writable backingStore;
//    private final Set<Readable> roStores;
//
//    public MultiBucketS3ObjectStore(final Writable backingStore, final Set<Readable> roStores) {
//        this.backingStore = backingStore;
//        this.roStores = roStores;
//    }
//
//}
//
//@Configuration
//public class S3ObjectStoreConfig {
//    @Bean
//    public Readable readableObjectStore(final Set<BucketId> bucketIds) {
//        return new S3ReadableS3ObjStore();
//    }
//
//    @Bean
//    public Writable writableObjectStore(final Set<BucketId> bucketIds) {
//        return new S3WritableS3ObjStore();
//    }
//
//    @Bean
//    public Listable listableObjectStore(final Set<BucketId> bucketIds) {
//        return new S3ListableS3ObjStore();
//    }
//
//    @Bean
//    public Deletable deletableObjectStore(final Set<BucketId> bucketIds) {
//        return new S3DeletableS3ObjStore();
//    }
//}
