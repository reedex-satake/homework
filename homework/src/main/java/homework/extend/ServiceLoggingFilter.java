package homework.extend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.jboss.logging.Logger;

/**
 * ロギングフィルタークラス。
 * @author satake
 */
@ServiceLogging
@Provider
public class ServiceLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

	/** ロガー */
	private Logger logger = Logger.getLogger(ServiceLoggingFilter.class);

	/**
	 * リクエストフィルター。
	 * <p>
	 * ログレベルがデバッグ以上の時にリクエストパラメータをログに出力する。
	 * </p>
	 */
	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		if (logger.isDebugEnabled()) {
			UriInfo uriInfo = request.getUriInfo();
			logger.debug("******************** Request ********************");
			logger.debug("  Method: " + request.getMethod());
			logger.debug("  Path:   " + uriInfo.getPath());
			logger.debug("  Header:");
			for (Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
				logger.debug("    " + entry.getKey() + ": " + entry.getValue());
			}
			if (!uriInfo.getQueryParameters().isEmpty()) {
				logger.debug("  Query Param:");
				for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet()) {
					logger.debug("    " + entry.getKey() + ": " + entry.getValue());
				}
			}
			if (!uriInfo.getPathParameters().isEmpty()) {
				logger.debug("  Path Param:");
				for (Entry<String, List<String>> entry : uriInfo.getPathParameters().entrySet()) {
					logger.debug("    " + entry.getKey() + ": " + entry.getValue());
				}
			}
			if (request.getEntityStream() != null) {
				logger.debug("  Entity Stream:");
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				IOUtils.copy(request.getEntityStream(), output);
				byte[] requestData = output.toByteArray();
				logger.debug("    " + new String(requestData, "UTF-8"));
				request.setEntityStream(new ByteArrayInputStream(requestData));
			}
			logger.debug("*************************************************");
		}
	}

	/**
	 * レスポンスフィルター。
	 * <p>
	 * ログレベルがデバッグ以上の時にレスポンスのステータスとエンティティをログに出力する。
	 * </p>
	 */
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("******************** Response ********************");
			logger.debug("  Status: " + response.getStatus());
			logger.debug("  Entity: " + response.getEntity());
			logger.debug("**************************************************");
		}
	}
}
