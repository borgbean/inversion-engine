package io.inversion.cloud.action.security;

import java.sql.Connection;
import java.util.Arrays;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import io.inversion.cloud.action.rest.RestAction;
import io.inversion.cloud.action.security.AuthAction.SqlDbUserDao;
import io.inversion.cloud.action.sql.H2SqlDb;
import io.inversion.cloud.action.sql.SqlDb;
import io.inversion.cloud.model.Api;
import io.inversion.cloud.model.User;
import io.inversion.cloud.service.Engine;
import io.inversion.cloud.utils.Rows;
import io.inversion.cloud.utils.Rows.Row;
import io.inversion.cloud.utils.SqlUtils;
import junit.framework.TestCase;

public class TestAuthAction extends TestCase
{

   @Test
   public void test() throws Exception
   {
   }

   //   /**
   //    * This simple factory method is static so that other  
   //    * demos can use and extend this api configuration.
   //    */
   //   public static Api buildApi()
   //   {
   //      SqlDb db = new H2SqlDb("db", "users.db", AuthAction.class.getResource("users-h2.ddl").toString(), TestAuthAction.class.getResource("test-users-h2.ddl").toString()).withCollectionPath("tables");
   //      SqlDbUserDao dao = new SqlDbUserDao(db);
   //
   //      AuthAction authAction = new AuthAction();
   //      authAction.withSalt("1tHbDUZ6RHXp0Xrgl59wo5mJEoCQbQm4");
   //      authAction.withDao(dao);
   //
   //      System.setProperty("jwt.secret", "SFPkBvLIgggezYmYUHJjWlPUbsPIq24B");
   //
   //      Api api = new Api("v1")//
   //                             .withName("users")//
   //                             .withDb(db)//
   //                             .withEndpoint("GET,PUT,POST,DELETE", "tables/*", new RestAction())//
   //                             .withEndpoint("GET,PUT,POST,DELETE", "auth", authAction);
   //
   //      api.withAccountCode("tests");
   //      api.withApiCode("v1");
   //
   //      api.putCache("authAction", authAction); //this is here so that test classes can easily get authAction to run tests
   //      api.putCache("dao", dao);
   //
   //      return api;
   //
   //   }
   //
   //   @Test
   //   public void testAuthAction001() throws Exception
   //   {
   //      Engine e = new Engine(buildApi());
   //      e.startup();
   //
   //      Api api = e.getApi("v1");
   //      SqlDbUserDao dao = (SqlDbUserDao) api.getCache("dao");
   //
   //      Connection conn = dao.getDb().getConnection();
   //      Rows rows = SqlUtils.selectRows(conn, "select * from User");
   //      System.out.println(rows);
   //      rows = SqlUtils.selectRows(conn, "select * from `Group`");
   //      System.out.println(rows);
   //      rows = SqlUtils.selectRows(conn, "select * from UserGroup");
   //      System.out.println(rows);
   //      rows = SqlUtils.selectRows(conn, "select * from Permission");
   //      System.out.println(rows);
   //      rows = SqlUtils.selectRows(conn, "select * from UserPermission");
   //      System.out.println(rows);
   //
   //      rows = dao.findGRP(conn, 10, "tests", "v1", null);
   //      for (Row row : rows)
   //      {
   //         System.out.println(row);
   //      }
   //
   //      assertTrue(findPermission(rows, "permission1", "user->permission"));
   //      assertTrue(findPermission(rows, "permission2", "user->permission"));
   //      assertTrue(findPermission(rows, "permission2", "user->group->permission"));
   //      assertTrue(findPermission(rows, "permission3", "user->group->permission"));
   //      assertTrue(findPermission(rows, "permission4", "user->group->role->permission"));
   //      assertTrue(findPermission(rows, "permission5", "user->role->permission"));
   //
   //      User u = dao.getUser("ct_admin", "tests", "v1", null);
   //
   //      assertEquals(0, CollectionUtils.disjunction(Arrays.asList("Administrator", "Member"), u.getRoles()).size());//u.hasRoles("Administrator"));
   //      assertEquals(0, CollectionUtils.disjunction(Arrays.asList("admin_users", "read_only"), u.getGroups()).size());
   //      assertEquals(0, CollectionUtils.disjunction(Arrays.asList("permission1", "permission2", "permission3", "permission4", "permission5"), u.getPermissions()).size());
   //
   //      //System.out.println(generateJwt(u, -1, null, null, null));
   //      //      //AuthAction authAction = (AuthAction)e.getApi("tests").getCache("authAction");
   //      //      Response res = null;
   //      //
   //      //      //      res = e.get("tests/tables/groups");
   //      //      //      res.dump();
   //      //      //
   //      //      //      res = e.get("tests/tables/userpermissions");
   //      //      //      res.dump();
   //      //      //
   //      //      //      res = e.get("tests/tables/apikeys");
   //      //      //      res.dump();
   //      //
   //      //      res = e.get("tests/tables/users?username=ct_admin&expands=groups,userpermissions,userroles,apiKeys");
   //      //      //res = e.get("tests/tables/users?expands=apiKeys");
   //      //      res.dump();
   //   }
   //
   //   boolean findPermission(Rows rows, String name, String via)
   //   {
   //      for (Row row : rows)
   //      {
   //         String type = row.getString("type");
   //         String n = row.getString("name");
   //         String v = row.getString("via");
   //
   //         if (type.equals("permission"))
   //         {
   //            if (name.equals(n) && via.equals(via))
   //               return true;
   //         }
   //      }
   //      return false;
   //   }
}
