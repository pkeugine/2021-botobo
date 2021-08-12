package botobo.core.exception.user.s3;

import botobo.core.exception.http.InternalServerErrorException;

public class FileConvertFailedException extends InternalServerErrorException {
    public FileConvertFailedException() {
        super("파일을 변환할 수 없습니다.");
    }
}
