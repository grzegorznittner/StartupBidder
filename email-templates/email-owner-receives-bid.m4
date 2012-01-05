changequote([[, ]])
include(template-fancy-variablelayout.m4)

# 1. Subject of the email
# 2. Plain text description, will be used as preview (if possible)
# 3. Link to "View in browser"
# 4. Title of the email (not subject!)
# 5. HTML description rendered below title
# 6. Additional info below description
# 7. email address of the mailing list
# 8. Link to the header image (eg. "Notificaiton from startupbidder")
# 9. Link to company's logo for which email was sent

fancyvariablelayout([[Your listing received a bid]],
  [[An accredited investor G...r offered $25,000 for 45% common stock for your listing "Social Recommendations".]],
  [[http://www.startupbidder.com/n/435634653.html]],
  [[Listing "Social Recommendation" received a bid]],
  [[An accredited investor <strong>G...r</strong> offered <strong>$25,000</strong> for <strong>45%</strong> common stock for your listing <strong>"Social Recommendations"</strong>.]],
  [[In order to accept, counter offer or reject this offer please visit <a href="http://www.startupbidder.com/l/2345346.html">company's page at startupbidder</a>.]],
  [[notifications@startupbidder.com]],
  [[http://img210.imageshack.us/img210/8564/emailnotification.jpg]],
  [[http://img804.imageshack.us/img804/6324/socialrec.jpg]])

