package com.krunal.example;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface InterfaceFileUpload {

    @Multipart
    @POST("Service/saveFile")
    Call<UploadResponse> UploadFile(@Part MultipartBody.Part file,
                                    @Part("CustomerID") RequestBody CustomerID,
                                    @Part("BackupType") RequestBody BackupType,
                                    @Part("ProductName") RequestBody ProductName,
                                    @Part("AppVersionAppVersion") RequestBody AppVersionAppVersion,
                                    @Part("AppType") RequestBody AppType,
                                    @Part("IMEINumber") RequestBody IMEINumber,
                                    @Part("DeviceInfo") RequestBody DeviceInfo,
                                    @Part("Remark") RequestBody Remark,
                                    @Part("FileName") RequestBody FileName,
                                    @Part("Extentsion") RequestBody Extentsion);

//    @Multipart
//    @POST("/upload_multi_files/fileUpload.php")
//    Call<UploadObject> uploadSingleFile(@Part MultipartBody.Part file, @Part("name") RequestBody name);

}
