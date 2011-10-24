function Uploader() {
			var self = {};
			self.prepareUploader = function(fieldRoot, fieldName) {
				var uploader = function() {
					var callback = function(uploadjson) {
						var actionUrl = uploadjson[0];
						var submitCallback = function() {
							var loadCallback = function() {
								var responseEl = $('#' + fieldRoot + '-iframe')
										.contents().find('body');
								var responseHtml;
								var responseId = undefined;
								var downloadUrl;
								if (responseEl) {
									responseHtml = responseEl.html().replace(
											/<[^>]*>/g, '');
									if (responseHtml) {
										responseId = responseHtml.replace(
												/^{ *"?id"? *: *"/, '')
												.replace(/".*$/, '');
									}
								}
								if (responseId === undefined
										|| responseId.length === 0) {
									$('#listing-save-message').attr('class',
											'confirmed')
											.html('error uploading');
								} else {
									downloadUrl = '/file/download/'
											+ responseId;
									$('#listing-save-message').attr('class',
											'confirmed').html(
											'upload successful');
									$('#' + fieldRoot + '-link').data(
											fieldName, responseId)
											.children('a').attr('href',
													downloadUrl);
									$('#' + fieldRoot + '-link').show();
								}
								$('#' + fieldRoot + '-file').hide().val('');
								$('#' + fieldRoot + '-submit').hide();
							};
							$('#listing-save-message').attr('class',
									'confirmed').html('uploading...');
							$('#' + fieldRoot + '-form').get(0).target = fieldRoot
									+ '-iframe';
							$('#' + fieldRoot + '-iframe').unbind().load(
									loadCallback);
						};
						$('#' + fieldRoot + '-form').attr('action', actionUrl)
								.unbind().submit(submitCallback).show();
						$('#' + fieldRoot + '-file').show();
					};
					var errorCallback = function() {
						$('#listing-save-message')
								.attr('class', 'attention')
								.html(
										'unable to obtain business plan upload data');
					};
					var fileChangeCallback = function() {
						var filename = $(this).val();
						if (!/\.doc|\.docx|\.pdf|\.ppt$/.test(filename)) {
							alert('Only doc, docx, ppt or pdf files are allowed for upload');
							$(this).val('');
							$('#' + fieldRoot + '-submit').hide();
						} else {
							$('#' + fieldRoot + '-submit').show();
						}
					};
					$('#' + fieldRoot + '-file').hide().val('').change(
							fileChangeCallback);
					$('#' + fieldRoot + '-submit').hide();
					self.imports.backend.getUploadUrls(1, callback, errorCallback);
				};
				return uploader;
			};
			return self;
}
