<% csimsoft.ResultMessage message =
       (csimsoft.ResultMessage)session.getAttribute("message");
   if(message != null)
   {
     if(!message.isEmpty())
     {
       String cssName = "info-msg";
       String defaultIcon = "/images/icon-info.jpg";
       if(message.getType() == csimsoft.ResultMessage.WarningMessage)
       {
         cssName = "warn-msg";
         defaultIcon = "/images/icon-warning.jpg";
       }
       else if(message.getType() == csimsoft.ResultMessage.ErrorMessage)
       {
         cssName = "error-msg";
         defaultIcon = "/images/icon-error.jpg";
       }

       String iconPath = message.getIcon();
       if(iconPath == null || iconPath.isEmpty())
         iconPath = defaultIcon;

       iconPath = request.getContextPath() + iconPath; %>
<div class="content-msg">
<table class="<%= cssName %>" cellpadding="8" cellspacing="0">
  <tr>
    <td class="msg-icon"><img src="<%= iconPath %>" width="40" height="40" /></td>
    <td class="<%= cssName %>"><%= message.getMessage() %></td>
  </tr>
</table>
</div>
<%   }
     message.clear();
   } %>
