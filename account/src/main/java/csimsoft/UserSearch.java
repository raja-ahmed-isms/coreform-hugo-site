/**
 * @(#)UserSearch.java
 *
 *
 * @author Mark Richardson
 * @version 1.00 2012/4/13
 */

package csimsoft;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;


public class UserSearch
{
   public static final String Separator = "/";

   private static final int StartState = 0;
   private static final int ParenLeftState = 1;
   private static final int NameState = 2;
   private static final int CombineState = 3;
   private static final int CompareState = 4;
   private static final int FirstValueState = 5;
   private static final int ValueState = 6;
   private static final int ParenRightState = 7;
   private static final int EndState = 8;

   private static final int WildCardStart = 0x01;
   private static final int WildCardEnd = 0x02;

   private static final int StringType = 1;
   private static final int IntType = 2;
   private static final int DateType = 3;

   private static final int UserTableSearch = 0;
   private static final int ProductTableSearch = 1;
   private static final int LicenseTableSearch = 2;
   private static final int OrderTableSearch = 4;
   private static final int OrderProductTableSearch = 5;
   private static final int OrderLicenseTableSearch = 6;

   private String Search = "";
   private ArrayList<UserSearch.SearchValue> Params =
      new ArrayList<UserSearch.SearchValue>();
   private int State = UserSearch.StartState;
   private int Nesting = 0;
   private int Wild = 0;
   private int ValueType = 0;
   private int SearchTable = UserSearch.UserTableSearch;
   private boolean NeedsCompare = false;
   private boolean NeedsSecondValue = false;

   public static class SearchValue
   {
      public int valueType = UserSearch.IntType;
      public String value = null;

      public SearchValue()
      {
      }

      public SearchValue(int type, String data)
      {
         this.valueType = type;
         this.value = data;
      }
   }

   public UserSearch()
   {
   }

   public String getSearch()
   {
      return this.Search;
   }

   public boolean setSearch(String search)
   {
      // SELECT userid, email, firstname, lastname FROM users
      // WHERE [(] userid [NOT] IN (SELECT userid FROM siteadmins) [)] AND/OR [(]
      // userid [NOT] IN (SELECT userid FROM distributors) [)] AND/OR [(]
      // groupid = 0 [)] AND/OR [(] email LIKE '%.jp' [)]
      this.Search = "";
      this.Params.clear();
      this.Nesting = 0;
      this.Wild = 0;
      this.ValueType = 0;
      this.SearchTable = UserSearch.UserTableSearch;
      this.NeedsCompare = false;
      this.NeedsSecondValue = false;
      this.State = UserSearch.StartState;
      String[] searchArray = search.split(UserSearch.Separator);
      int i = 0;
      for( ; i < searchArray.length; ++i)
      {
         // Allowed states and transitions:
         // start -> name
         // start -> pl -> name
         // name -> end
         // name -> pr -> end
         // name -> combine -> name
         // name -> compare -> value
         // name -> compare -> value -> value
         // value -> combine -> name
         // value -> end
         // value -> pr -> end
         switch(this.State)
         {
            case UserSearch.StartState:
            {
               if(processOpen(searchArray[i]))
                  break;
            }
            case UserSearch.ParenLeftState:
            case UserSearch.CombineState:
            {
               if(!processName(searchArray[i]))
                  return false;

               break;
            }
            case UserSearch.NameState:
            case UserSearch.ValueState:
            {
               if(processClose(searchArray[i]))
                  break;

               if(this.State == UserSearch.NameState && this.NeedsCompare)
               {
                  if(!processCompare(searchArray[i]))
                     return false;
               }
               else
               {
                  if(!processCombine(searchArray[i]))
                     return false;
               }

               break;
            }
            case UserSearch.CompareState:
            case UserSearch.FirstValueState:
            {
               processValue(searchArray[i]);
               break;
            }
            case UserSearch.ParenRightState:
            {
               if(!processCloseSubquery(searchArray[i]))
                  this.State = UserSearch.EndState;

               break;
            }
            default:
               return false;
         }

         if(this.State == UserSearch.EndState)
            break;
      }

      // Make sure the search code terminated correctly.
      if(i < searchArray.length || this.Nesting != 0)
         return false;
      else if(this.State < UserSearch.NameState || (this.State > UserSearch.NameState &&
         this.State < UserSearch.ValueState))
      {
         return false;
      }

      // Prepend the rest of the search query.
      this.Search = "SELECT userid, email, firstname, lastname, company, phone," +
         " country FROM users WHERE " + this.Search +
         " ORDER BY lastname, firstname, email";

      return true;
   }

   public void executeQuery(Database database) throws SQLException
   {
      if(this.Search.isEmpty())
         return;

      PreparedStatement statement = database.connection.prepareStatement(this.Search,
         ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      database.addStatement(statement);
      Iterator<UserSearch.SearchValue> iter = this.Params.iterator();
      for(int i = 1; iter.hasNext(); ++i)
      {
         UserSearch.SearchValue param = iter.next();
         if(param.valueType == UserSearch.IntType)
            statement.setInt(i, Integer.parseInt(param.value));
         else
            statement.setString(i, param.value);
      }

      database.results = statement.executeQuery();
   }

   private boolean processOpen(String token)
   {
      if(token.equalsIgnoreCase("pl"))
      {
         this.Search += "(";
         ++this.Nesting;
         this.State = UserSearch.ParenLeftState;
         return true;
      }

      return false;
   }

   private boolean processClose(String token)
   {
      if(token.equalsIgnoreCase("pr"))
      {
         this.Search += ")";
         --this.Nesting;
         this.State = UserSearch.ParenRightState;
         return true;
      }

      return this.processCloseSubquery(token);
   }

   private boolean processCloseSubquery(String token)
   {
      if((this.SearchTable == UserSearch.ProductTableSearch &&
         token.equalsIgnoreCase("ppr")) ||
         (this.SearchTable == UserSearch.LicenseTableSearch &&
         token.equalsIgnoreCase("lpr")) ||
         (this.SearchTable == UserSearch.OrderTableSearch &&
         token.equalsIgnoreCase("opr")) ||
         (this.SearchTable == UserSearch.OrderProductTableSearch &&
         token.equalsIgnoreCase("oppr")) ||
         (this.SearchTable == UserSearch.OrderLicenseTableSearch &&
         token.equalsIgnoreCase("olpr")))
      {
         this.State = UserSearch.NameState;
         if(this.SearchTable == UserSearch.OrderLicenseTableSearch)
            this.Search += "))";
         else
            this.Search += ")";

         if(this.SearchTable == UserSearch.OrderProductTableSearch ||
            this.SearchTable == UserSearch.OrderLicenseTableSearch)
         {
            this.SearchTable = UserSearch.OrderTableSearch;
         }
         else
            this.SearchTable = UserSearch.UserTableSearch;

         this.NeedsCompare = false;
         return true;
      }

      return false;
   }

   private boolean processName(String token)
   {
      // Name = admin, notadmin, dist, notdist, email, firstname, lastname,
      //   groupid, company, phone, address, country, productid, licenseid,
      //   expires
      this.ValueType = UserSearch.StringType;
      this.NeedsCompare = true;
      if(this.SearchTable == UserSearch.UserTableSearch)
      {
         if(token.equalsIgnoreCase("admin"))
         {
            this.Search += "userid IN (SELECT userid FROM siteadmins)";
            this.NeedsCompare = false;
         }
         else if(token.equalsIgnoreCase("notadmin"))
         {
            this.Search += "userid NOT IN (SELECT userid FROM siteadmins)";
            this.NeedsCompare = false;
         }
         else if(token.equalsIgnoreCase("dist"))
         {
            this.Search += "userid IN (SELECT userid FROM distributors where reseller = 0)";
            this.NeedsCompare = false;
         }
         else if(token.equalsIgnoreCase("notdist"))
         {
            this.Search += "userid NOT IN (SELECT userid FROM distributors where reseller = 0)";
            this.NeedsCompare = false;
         }
         else if(token.equalsIgnoreCase("reseller"))
         {
            this.Search += "userid IN (SELECT userid FROM distributors where reseller = 1)";
            this.NeedsCompare = false;
         }
         else if(token.equalsIgnoreCase("notreseller"))
         {
            this.Search += "userid NOT IN (SELECT userid FROM distributors where reseller = 1)";
            this.NeedsCompare = false;
         }
         else if(token.equalsIgnoreCase("email"))
            this.Search += "email";
         else if(token.equalsIgnoreCase("firstname"))
            this.Search += "firstname";
         else if(token.equalsIgnoreCase("lastname"))
            this.Search += "lastname";
         else if(token.equalsIgnoreCase("groupid"))
         {
            this.ValueType = UserSearch.IntType;
            this.Search += "groupid";
         }
         else if(token.equalsIgnoreCase("company"))
            this.Search += "company";
         else if(token.equalsIgnoreCase("phone"))
            this.Search += "phone";
         else if(token.equalsIgnoreCase("address"))
            this.Search += "address";
         else if(token.equalsIgnoreCase("country"))
            this.Search += "country";
         else if(token.equalsIgnoreCase("ppl"))
         {
            this.State = UserSearch.StartState;
            this.SearchTable = UserSearch.ProductTableSearch;
            this.Search += "userid IN (SELECT userid FROM userdownloads WHERE ";
            return true;
         }
         else if(token.equalsIgnoreCase("lpl"))
         {
            this.State = UserSearch.StartState;
            this.SearchTable = UserSearch.LicenseTableSearch;
            this.Search += "userid IN (SELECT userid FROM licenses WHERE ";
            return true;
         }
         else if(token.equalsIgnoreCase("opl"))
         {
            this.State = UserSearch.StartState;
            this.SearchTable = UserSearch.OrderTableSearch;
            this.Search += "userid IN (SELECT userid FROM userorders WHERE ";
            return true;
         }
         else
            return false;
      }
      else if(this.SearchTable == UserSearch.ProductTableSearch)
      {
         if(token.equalsIgnoreCase("productid"))
         {
            this.ValueType = UserSearch.IntType;
            this.Search += "productid";
         }
         else
            return false;
      }
      else if(this.SearchTable == UserSearch.LicenseTableSearch)
      {
         if(token.equalsIgnoreCase("licenseid"))
         {
            this.ValueType = UserSearch.IntType;
            this.Search += "licenseid";
         }
         else if(token.equalsIgnoreCase("expires"))
         {
            this.ValueType = UserSearch.DateType;
            this.Search += "expiration";
         }
         else
            return false;
      }
      else if(this.SearchTable == UserSearch.OrderTableSearch)
      {
         if(token.equalsIgnoreCase("orderdate"))
         {
            this.ValueType = UserSearch.DateType;
            this.Search += "orderdate";
         }
         else if(token.equalsIgnoreCase("oppl"))
         {
            this.ValueType = UserSearch.IntType;
            this.SearchTable = UserSearch.OrderProductTableSearch;
            this.Search +=
               "orderid IN (SELECT orderid FROM orderproducts WHERE productid";
         }
         else if(token.equalsIgnoreCase("olpl"))
         {
            this.ValueType = UserSearch.IntType;
            this.SearchTable = UserSearch.OrderLicenseTableSearch;
            this.Search +=
               "orderid IN (SELECT orderid FROM orderlicenses WHERE licenseid IN " +
               "(SELECT userlicenseid FROM licenses WHERE licenseid";
         }
         else
            return false;
      }
      else
         return false;

      this.State = UserSearch.NameState;
      return true;
   }

   private boolean processCompare(String token)
   {
      // Compare = e, ne, gt, gte, lt, lte, en, nen, b,
      //   l, nl, sw, nsw, ew, new
      this.Wild = 0;
      this.NeedsCompare = false;
      this.NeedsSecondValue = false;
      if(token.equalsIgnoreCase("e"))
         this.Search += " = ";
      else if(token.equalsIgnoreCase("ne"))
         this.Search += " <> ";
      else if(token.equalsIgnoreCase("gt"))
         this.Search += " > ";
      else if(token.equalsIgnoreCase("gte"))
         this.Search += " >= ";
      else if(token.equalsIgnoreCase("lt"))
         this.Search += " < ";
      else if(token.equalsIgnoreCase("lte"))
         this.Search += " <= ";
      else if(token.equalsIgnoreCase("en"))
      {
         this.Search += " IS NULL ";
         this.State = UserSearch.ValueState;
         return true;
      }
      else if(token.equalsIgnoreCase("nen"))
      {
         this.Search += " IS NOT NULL ";
         this.State = UserSearch.ValueState;
         return true;
      }
      else if(token.equalsIgnoreCase("b"))
      {
         this.Search += " BETWEEN ";
         this.NeedsSecondValue = true;
      }
      else if(this.ValueType != UserSearch.StringType)
         return false;
      else if(token.equalsIgnoreCase("l"))
      {
         this.Search += " LIKE ";
         this.Wild = UserSearch.WildCardStart | UserSearch.WildCardEnd;
      }
      else if(token.equalsIgnoreCase("nl"))
      {
         this.Search += " NOT LIKE ";
         this.Wild = UserSearch.WildCardStart | UserSearch.WildCardEnd;
      }
      else if(token.equalsIgnoreCase("sw"))
      {
         this.Search += " LIKE ";
         this.Wild = UserSearch.WildCardEnd;
      }
      else if(token.equalsIgnoreCase("nsw"))
      {
         this.Search += " NOT LIKE ";
         this.Wild = UserSearch.WildCardEnd;
      }
      else if(token.equalsIgnoreCase("ew"))
      {
         this.Search += " LIKE ";
         this.Wild = UserSearch.WildCardStart;
      }
      else if(token.equalsIgnoreCase("new"))
      {
         this.Search += " NOT LIKE ";
         this.Wild = UserSearch.WildCardStart;
      }
      else
         return false;

      this.State = UserSearch.CompareState;
      return true;
   }

   private boolean processCombine(String token)
   {
      // Combine = a, o, pl, pr, apl, pra, prapl, opl, pro, propl
      // Keep track of parenthesis nesting.
      if(token.equalsIgnoreCase("a"))
         this.Search += " AND ";
      else if(token.equalsIgnoreCase("o"))
         this.Search += " OR ";
      else if(token.equalsIgnoreCase("apl"))
      {
         this.Search += " AND (";
         ++this.Nesting;
      }
      else if(token.equalsIgnoreCase("pra"))
      {
         this.Search += ") AND ";
         --this.Nesting;
      }
      else if(token.equalsIgnoreCase("prapl"))
         this.Search += ") AND (";
      else if(token.equalsIgnoreCase("opl"))
      {
         this.Search += " OR (";
         ++this.Nesting;
      }
      else if(token.equalsIgnoreCase("pro"))
      {
         this.Search += ") OR ";
         --this.Nesting;
      }
      else if(token.equalsIgnoreCase("propl"))
         this.Search += ") OR (";
      else
         return false;

      this.State = UserSearch.CombineState;
      return true;
   }

   private void processValue(String token)
   {
      // Value = string, int, or date
      UserSearch.SearchValue param = new SearchValue(this.ValueType, token);
      this.Params.add(param);
      this.Search += "?";
      if(this.ValueType == UserSearch.StringType)
      {
         if((this.Wild & UserSearch.WildCardStart) > 0)
            param.value = "%" + param.value;

         if((this.Wild & UserSearch.WildCardEnd) > 0)
            param.value += "%";
      }

      this.Wild = 0;
      if(this.NeedsSecondValue)
      {
         this.NeedsSecondValue = false;
         this.Search += " AND ";
         this.State = UserSearch.FirstValueState;
      }
      else
         this.State = UserSearch.ValueState;
   }

   public static boolean isDistributorValid(String query, int groupId)
   {
      // Make sure the distributor can run the query. The query should
      // have the distributor's group id as one of the parameters. There
      // should not be any other group id specified.
      String group = "groupid";
      int index = query.indexOf(group, 0);
      boolean valid = index > -1;
      if(valid)
      {
         String validId = UserSearch.Separator + "e" + UserSearch.Separator + groupId;
         int index2 = query.indexOf(validId, index);
         valid = index2 == index + group.length();
         if(valid)
            valid = query.indexOf(group, index + 1) == -1;
      }

      return valid;
   }
}
