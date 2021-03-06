package com.omnie.controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.impetus.kundera.KunderaException;
import com.impetus.kundera.loader.KunderaAuthenticationException;
import com.mongodb.MongoExecutionTimeoutException;
import com.omnie.model.service.ImageStorageService;
import com.omnie.module.error.handler.ErrorMessage;
import com.omnie.module.utils.Message;
import play.Logger;
import play.cache.CacheApi;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Harmeet Singh(Taara) on 27/12/16.
 */
@Singleton
public class ImageController extends Controller {

	private CacheApi cache;


	private ImageStorageService imageStorageService;

	private Logger logger;

	@Inject
	public ImageController( ImageStorageService imageStorageService,  CacheApi cache, Logger logger ) {
		this.imageStorageService = imageStorageService;
		this.cache = cache;
		this.logger = logger;
	}

	public Result uploadImage( ) throws IOException, ExecutionException, InterruptedException {

		Http.MultipartFormData< File > body = request( ).body( ).asMultipartFormData( );
		Http.MultipartFormData.FilePart< File > picture = body.getFile( "picture" );
		if ( picture != null ) {
			Logger.info( "File uploaded ::: size = " + picture.getFile().length());
			if (picture.getFile().exists()){
				double bytes = picture.getFile().length();
				double kilobytes = (bytes / 1024);
				double megabytes = (kilobytes / 1024);
				if (megabytes > 2.5) {
					return ok( Json.toJson( new ErrorMessage(307, "File limit ::: 2.5MB" ) ) );
				}
			}
			try {
				return ok( Json.toJson( new Message(
					200,
					imageStorageService.prepareAndSave( picture ).getImageId( ) )
				) );
			} catch ( MongoExecutionTimeoutException | KunderaException e ) {
				e.printStackTrace();
				return ok( Json.toJson( new ErrorMessage(500, "Service unavailable" ) ) );
			}
		} else {
			flash( "error", "Missing file" );
			return ok( Json.toJson( new ErrorMessage("Picture is invalid") ) );
		}
	}
	public Result deleteImage( String objectId ) {
		imageStorageService.delete( objectId );
		return ok( objectId );
	}
	public Result deleteImages(){
		List<String> images =  (List<String>) Json.fromJson( request().body().asJson() , List.class );
		imageStorageService.multipleDelete( images );
		return ok(Json.toJson( images ));
	}

	public Result getImageSource( String objectId, String imageType )
			throws IOException, ExecutionException, InterruptedException {
		String _objectId = objectId;
		String _type = imageType;
		String cacheKey = objectId + imageType;

		File image = cache.getOrElse( cacheKey, () -> {

			File imageT = imageStorageService.getTypedImageById( _objectId, _type );
			cache.set( cacheKey , imageT, 60 * 15 );
			return imageT;
		} );

		return ok( image );
	}
}
