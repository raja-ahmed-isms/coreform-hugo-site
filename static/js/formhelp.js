
function stripString(str)
{
  // Find the first non white space character.
  var i = 0
  for( ; i < str.length; i++)
  {
    if(str.charAt(i) != ' ')
    {
      break;
    }
  }

  // Strip off the left white space.
  if(i > 0)
  {
    str = str.substring(i)
  }

  // Find the first non white space character from the end.
  for(i = str.length - 1; i >= 0; i--)
  {
    if(str.charAt(i) != ' ')
    {
      break;
    }
  }

  // Strip off the right white space.
  if(i < str.length - 1)
  {
    str = str.substring(0, i + 1)
  }

  return str
}

function isInputPosInteger(item)
{
  var str = item.value.toString()
  for(var i = 0; i < str.length; i++)
  {
    var oneChar = str.charAt(i)
    if(oneChar < '0' || oneChar > '9')
      return false
  }

  return true
}

function isInputEmpty(item)
{
  return item.value.length == 0
}

function stripAndValidateInput(item)
{
  item.value = stripString(item.value)
  return isInputEmpty(item)
}

function stripAndValidatePosInteger(item, allowEmpty)
{
  item.value = stripString(item.value)
  if(isInputEmpty(item))
  {
    if(allowEmpty)
    {
      item.value = "0"
      return false
    }

    return true
  }

  return !isInputPosInteger(item)
}

function validateLogin()
{
  if(stripAndValidateInput(document.loginform.user))
  {
    alert("The email address field is empty.\n" +
          "Please enter a valid email address.")
    document.loginform.user.focus()
    return false
  }

  if(isInputEmpty(document.loginform.pass))
  {
    alert("The password field is empty.\n" +
          "You must enter your password in order to log in.")
    document.loginform.pass.focus()
    return false
  }

  return true
}

function focusLoginForm()
{
  document.loginform.user.focus()
}
