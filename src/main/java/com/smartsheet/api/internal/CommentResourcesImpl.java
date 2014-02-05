package com.smartsheet.api.internal;

/*
 * #[license]
 * Smartsheet SDK for Java
 * %%
 * Copyright (C) 2014 Smartsheet
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * %[license]
 */



import com.smartsheet.api.AssociatedAttachmentResources;
import com.smartsheet.api.CommentResources;
import com.smartsheet.api.SmartsheetException;
import com.smartsheet.api.models.Comment;

/**
 * This is the implementation of the CommentResources.
 * 
 * Thread Safety: This class is thread safe because it is immutable and its base class is thread safe.
 */
public class CommentResourcesImpl extends AbstractResources implements CommentResources {
	/**
	 * Represents the AssociatedAttachmentResources.
	 * 
	 * It will be initialized in constructor and will not change afterwards.
	 */
	private AssociatedAttachmentResources attachments;

	/**
	 * Constructor.
	 * 
	 * Parameters: - smartsheet : the SmartsheetImpl
	 * 
	 * Exceptions: - IllegalArgumentException : if any argument is null
	 * 
	 * Implementation: super(smartsheet); this.attachments = new CommentAttachmentResources(smartsheet);
	 * 
	 * @param smartsheet
	 */
	public CommentResourcesImpl(SmartsheetImpl smartsheet) {
		super(smartsheet);
		this.attachments = new CommentAttachmentResources(smartsheet);
	}

	/**
	 * Get a comment.
	 * 
	 * It mirrors to the following Smartsheet REST API method: GET /comment/{id}
	 * 
	 * Parameters: - id : the ID
	 * 
	 * Returns: the resource (note that if there is no such resource, this method will throw ResourceNotFoundException
	 * rather than returning null).
	 * 
	 * Exceptions: - InvalidRequestException : if there is any problem with the REST API request -
	 * AuthorizationException : if there is any problem with the REST API authorization(access token) -
	 * ResourceNotFoundException : if the resource can not be found - ServiceUnavailableException : if the REST API
	 * service is not available (possibly due to rate limiting) - SmartsheetRestException : if there is any other REST
	 * API related error occurred during the operation - SmartsheetException : if there is any other error occurred
	 * during the operation
	 * 
	 * Implementation: return this.getResource("comment/" + id", Comment.class);
	 * 
	 * @param id
	 * @return
	 * @throws SmartsheetException 
	 */
	public Comment getComment(long id) throws SmartsheetException {
		return this.getResource("comment/" + id, Comment.class);
	}

	/**
	 * Delete a comment.
	 * 
	 * It mirrors to the following Smartsheet REST API method: DELETE /comment{id}
	 * 
	 * Parameters: - id : the ID of the comment
	 * 
	 * Returns: None
	 * 
	 * Exceptions: - InvalidRequestException : if there is any problem with the REST API request -
	 * AuthorizationException : if there is any problem with the REST API authorization(access token) -
	 * ResourceNotFoundException : if the resource can not be found - ServiceUnavailableException : if the REST API
	 * service is not available (possibly due to rate limiting) - SmartsheetRestException : if there is any other REST
	 * API related error occurred during the operation - SmartsheetException : if there is any other error occurred
	 * during the operation
	 * 
	 * Implementation: this.deleteResource("comment/" + id, Comment.class);
	 * 
	 * @param id
	 * @throws SmartsheetException 
	 */
	public void deleteComment(long id) throws SmartsheetException {
		this.deleteResource("comment/" + id, Comment.class);
	}

	/**
	 * Return the AssociatedAttachmentResources object that provides access to attachment resources associated with
	 * Comment resources.
	 * 
	 * Returns: the AssociatedAttachmentResources object
	 * 
	 * Exceptions: None
	 * 
	 * Implementation: return this.attachments;
	 * 
	 * @return
	 */
	public AssociatedAttachmentResources attachments() {
		return this.attachments;
	}
}
