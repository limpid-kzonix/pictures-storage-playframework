package com.omnie.module.error.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import play.Configuration;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Provider;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by limpid on 5/4/17.
 */
@Singleton
public class ErrorHandler extends DefaultHttpErrorHandler {

	@Inject
	public ErrorHandler( Configuration configuration, Environment environment,
	                     OptionalSourceMapper sourceMapper, Provider<Router > routes ) {
		super(configuration, environment, sourceMapper, routes);
	}

	protected CompletionStage<Result > onProdServerError( Http.RequestHeader request, UsefulException exception ) {
		return CompletableFuture.completedFuture(
				Results.internalServerError( "A server error occurred: " + exception.getMessage( ) )
		                                        );
	}

	protected CompletionStage<Result> onForbidden( Http.RequestHeader request, String message ) {
		return CompletableFuture.completedFuture(
				Results.forbidden("You're not allowed to access this resource.")
		                                        );
	}
}