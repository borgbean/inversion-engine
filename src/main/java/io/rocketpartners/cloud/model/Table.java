/*
 * Copyright (c) 2016-2019 Rocket Partners, LLC
 * http://rocketpartners.io
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rocketpartners.cloud.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.rocketpartners.cloud.utils.Rows;
import io.rocketpartners.cloud.utils.Rows.Row;
import io.rocketpartners.cloud.utils.Utils;

public class Table
{
   protected Db                db      = null;
   protected String            name    = null;
   protected ArrayList<Column> columns = new ArrayList();
   protected ArrayList<Index>  indexes = new ArrayList();

   protected boolean           exclude = false;

   public Table()
   {
      super();
   }

   public Table(Db db, String name)
   {
      super();
      this.db = db;
      this.name = name;
   }

   /**
    * @return the linkTbl
    */
   public boolean isLinkTbl()
   {
      boolean isLinkTbl = true;
      for (Column c : columns)
      {
         if (!c.isFk())
         {
            isLinkTbl = false;
            break;
         }
      }
      //System.out.println("IS LINK TABLE: " + name + " -> " + isLinkTbl);

      return isLinkTbl;
   }

   public Column getColumn(String name)
   {
      for (Column col : columns)
      {
         if (name.equalsIgnoreCase(col.getName()))
            return col;
      }
      return null;
   }

   public String toString()
   {
      return name != null ? name : super.toString();
   }

   /**
    * @return the db
    */
   public Db getDb()
   {
      return db;
   }

   /**
    * @param db the db to set
    */
   public Table withDb(Db db)
   {
      this.db = db;
      return this;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public Table withName(String name)
   {
      this.name = name;
      return this;
   }

   /**
    * @return the columns
    */
   public List<Column> getColumns()
   {
      ArrayList cols = new ArrayList(columns);
      Collections.sort(cols);
      return cols;
   }

   /**
    * @param columns the columns to set
    */
   public Table withColumns(List<Column> cols)
   {
      for (Column col : cols)
         withColumn(col);
      return this;
   }

   public Table withColumn(Column column)
   {
      Column existing = getColumn(column.getName());
      if (existing != null)
         throw new ApiException("you are trying to add a column name that already exists: " + column.getName());

      if (column != null && !columns.contains(column))
      {
         columns.add(column);
         column.withTable(this);
      }
      return this;
   }

   public Table withColumn(String name, String type)
   {
      Column column = getColumn(name);

      if (column == null)
      {
         column = new Column();
         columns.add(column);
      }
      column.withName(name);
      column.withType(type);

      return this;
   }

   public void removeColumn(Column column)
   {
      columns.remove(column);
   }

   public String getKeyName()
   {
      Index index = getPrimaryIndex();
      if (index != null && index.getColumns().size() == 1)
         return index.getColumns().get(0).getName();

      return null;
   }

   public String encodeKey(Map<String, Object> values)
   {
      Index index = getPrimaryIndex();
      if (index == null)
         return null;

      StringBuffer key = new StringBuffer("");
      for (Column col : index.getColumns())
      {
         Object val = values.get(col.getName());
         if (Utils.empty(val))
            return null;

         val = val.toString().replace("\\", "\\\\").replace("~", "\\~").replaceAll(",", "\\,");

         if (key.length() > 0)
            key.append("~");

         key.append(val);
      }

      return key.toString();
   }

   public static String encodeKey(List pieces)
   {
      StringBuffer entityKey = new StringBuffer("");
      for (int i = 0; i < pieces.size(); i++)
      {
         Object piece = pieces.get(i);
         if (piece == null)
            throw new ApiException(SC.SC_500_INTERNAL_SERVER_ERROR, "Trying to encode an entity key with a null component: '" + pieces + "'");

         entityKey.append(piece.toString().replace("\\", "\\\\").replace("~", "\\~").replaceAll(",", "\\,"));
         if (i < pieces.size() - 1)
            entityKey.append("~");
      }
      return entityKey.toString();
   }

   public Row decodeKey(String inKey)
   {
      return decodeKeys(inKey).iterator().next();
   }

   //parses val1~val2,val3~val4,val5~valc6
   public Rows decodeKeys(String inKeys)
   {
      String entityKeys = inKeys;
      Index index = getPrimaryIndex();
      if (index == null)
         throw new ApiException("Table '" + this.getName() + "' does not have a unique index");

      List<Column> columns = index.getColumns();

      List colNames = new ArrayList();
      columns.forEach(c -> colNames.add(c.getName()));
      Rows rows = new Rows(colNames);

      List<String> splits = new ArrayList();

      boolean escaped = false;
      for (int i = 0; i < entityKeys.length(); i++)
      {
         char c = entityKeys.charAt(i);
         switch (c)
         {
            case '\\':
               escaped = !escaped;
               continue;
            case ',':
               if (!escaped)
               {
                  rows.addRow(splits);
                  splits = new ArrayList();
                  entityKeys = entityKeys.substring(i + 1, entityKeys.length());
                  i = 0;
                  continue;
               }
            case '~':
               if (!escaped)
               {
                  splits.add(entityKeys.substring(0, i));
                  entityKeys = entityKeys.substring(i + 1, entityKeys.length());
                  i = 0;
                  continue;
               }
            default :
               escaped = false;
         }
      }
      if (entityKeys.length() > 0)
      {
         splits.add(entityKeys);
      }

      if (splits.size() > 0)
      {
         rows.addRow(splits);
      }

      for (Row row : rows)
      {
         if (row.size() != columns.size())
            throw new ApiException(SC.SC_400_BAD_REQUEST, "Supplied entity key '" + inKeys + "' has " + row.size() + "' parts but the primary index for table '" + this.getName() + "' has " + columns.size());

         for (int i = 0; i < columns.size(); i++)
         {
            Object value = row.getString(i).replace("\\\\", "\\").replace("\\~", "~").replace("\\,", ",");

            if (((String) value).length() == 0)
               throw new ApiException(SC.SC_400_BAD_REQUEST, "A key component can not be empty '" + inKeys + "'");

            value = getDb().cast(columns.get(i), value);
            row.set(i, value);
         }
      }

      return rows;
   }

   public Index getPrimaryIndex()
   {
      Index found = null;
      for (Index index : indexes)
      {
         if (!index.isUnique())
            continue;

         if (index.getColumns().size() == 0)
            return index;

         if (found == null)
         {
            found = index;
         }
         else if (index.getColumns().size() < found.getColumns().size())
         {
            found = index;
         }
      }
      return found;
   }

   public Index getIndex(String indexName)
   {
      for (Index index : indexes)
      {
         if (indexName.equalsIgnoreCase(index.getName()))
            return index;
      }
      return null;
   }

   public ArrayList<Index> getIndexes()
   {
      return new ArrayList(indexes);
   }

   public List<Index> getIndexes(String column)
   {
      List<Index> found = new ArrayList();
      for (Index index : indexes)
      {
         if (index.hasColumn(column))
            found.add(index);
      }
      return found;
   }

   public Table withIndexes(ArrayList<Index> indexes)
   {
      for (Index index : indexes)
         withIndex(index);

      return this;
   }

   public Table withIndex(Index index)
   {
      if (index != null && !indexes.contains(index))
         indexes.add(index);

      return this;
   }

   public Index withIndex(Column column, String name, String type, boolean unique)
   {
      //System.out.println("WITH INDEX: " + name + " - " + column);
      Index index = getIndex(name);
      if (index != null)
      {
         index.withColumn(column);
      }
      else
      {
         index = new Index(this, column, name, type, unique);
         withIndex(index);
      }
      return index;
   }

   public void removeIndex(Index index)
   {
      indexes.remove(index);
   }

   public boolean isExclude()
   {
      return exclude;
   }

   public Table withExclude(boolean exclude)
   {
      this.exclude = exclude;
      return this;
   }

}
