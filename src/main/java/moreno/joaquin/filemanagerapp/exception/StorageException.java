package moreno.joaquin.filemanagerapp.exception;

public class StorageException  extends RuntimeException{

    public StorageException (String message){
        super(message);
    }

    public StorageException(String message, Throwable throwable){
        super(message,throwable);
    }
}
