/*
 * This software is distributed under "GNU General Public License v3.0".
 * This license allows you to use it and/or modify it but you are not at
 * all allowed to sell this plugin at any cost. If found doing so the
 * necessary action required would be taken.
 *
 * FuturePlots is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License v3.0 for more details.
 *
 * You should have received a copy of the GNU General Public License v3.0
 * along with this program. If not, see
 * <https://opensource.org/licenses/GPL-3.0>.
 */

package tim03we.futureplots.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import tim03we.futureplots.FuturePlots;
import tim03we.futureplots.utils.Plot;
import tim03we.futureplots.utils.Settings;

@SuppressWarnings("unchecked")
public class MongoDBProvider implements DataProvider {
	
	private static final String COLLECTION_PLOTS = "plots";

	private FuturePlots plugin;

	private String databaseName;
	protected String databaseConnectionString;
	protected MongoClient mongoClient;

	@Override
	public void connect() {
		plugin = FuturePlots.getInstance();
		try {
			Class.forName("com.mongodb.MongoClient");
			String user = plugin.getConfig().getString("mongodb.user", "");
			String passwd = plugin.getConfig().getString("mongodb.password", "");
			String host = plugin.getConfig().getString("mongodb.host", "");
			String port = plugin.getConfig().getString("mongodb.port", "");
			this.databaseName = plugin.getConfig().getString("mongodb.database", "");
			databaseConnectionString = "mongodb://" + user + ":" + passwd + "@" + host + ":" + port + "/" + databaseName;
			establishConnectionAndRun();
			Server.getInstance().getLogger().info("[FuturePlots] Connection to MongoDB database successful.");
		} catch (ClassNotFoundException ex) {
			Server.getInstance().getLogger().error("[FuturePlots] No connection to the database could be established.");
			ex.printStackTrace();
		}
	}

	@Override
	public void save() {
	}

	@Override
	public void claimPlot(String name, Plot plot) {
		CompletableFuture.runAsync(() -> {
			try {
				Document toInsert = buildDocument(plot.getFullID(), new Object[][] {
					{Entries.LEVEL.getValue(), plot.getLevelName()},
					{Entries.PLOT_ID.getValue(), plot.getFullID()},
					{Entries.OWNER.getValue(), name}
				});
				insertDocument(COLLECTION_PLOTS, toInsert);
			} catch (Exception e) {
				if(Settings.debug) e.printStackTrace();
			}
		});
	}

	@Override
	public void deletePlot(Plot plot) {
		CompletableFuture.runAsync(() -> {
			deleteOne(COLLECTION_PLOTS, new Object[][] {
				{Entries.LEVEL.getValue(), plot.getLevelName()},
				{Entries.PLOT_ID.getValue(), plot.getFullID()}
			});
		});
	}

	@Override
    public boolean isHelper(String name, Plot plot) {
        return getHelpers(plot) != null && getHelpers(plot).contains(name.toLowerCase());
    }

    @Override
    public boolean isDenied(String name, Plot plot) {
        return getDenied(plot) != null && getDenied(plot).contains(name.toLowerCase());
    }

    @Override
    public boolean isMember(String name, Plot plot) {
        return getMembers(plot) != null && getMembers(plot).contains(name.toLowerCase());
    }

    @Override
    public boolean hasOwner(Plot plot) {
        return !getOwner(plot).equals("none");
    }

	@Override
	public void setOwner(String name, Plot plot) {
		CompletableFuture.runAsync(() -> {
			replaceProperty(COLLECTION_PLOTS, plot.getFullID(), Entries.OWNER.getValue(), name);
		});
	}

	@Override
	public String getOwner(Plot plot) {
		return getDocument(COLLECTION_PLOTS, Entries.PLOT_ID.getValue(), plot.getFullID()).getString(Entries.OWNER.getValue());
	}

	@Override
	public List<String> getHelpers(Plot plot) {
		try {
			List<String> toReturn = (List<String>) getDocument(COLLECTION_PLOTS, Entries.PLOT_ID.getValue(), plot.getFullID()).get(Entries.HELPERS.getValue());
			return toReturn != null ? toReturn : new ArrayList<String>();
		} catch (Exception e) {
			if(Settings.debug) e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	@Override
	public List<String> getMembers(Plot plot) {
		try {
			List<String> toReturn = (List<String>) getDocument(COLLECTION_PLOTS, Entries.PLOT_ID.getValue(), plot.getFullID()).get(Entries.MEMBERS.getValue());
			return toReturn != null ? toReturn : new ArrayList<String>();
		} catch (Exception e) {
			if(Settings.debug) e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	@Override
	public List<String> getDenied(Plot plot) {
		try {
			List<String> toReturn = (List<String>) getDocument(COLLECTION_PLOTS, Entries.PLOT_ID.getValue(), plot.getFullID()).get(Entries.DENIED.getValue());
			return toReturn != null ? toReturn : new ArrayList<String>();
		} catch (Exception e) {
			if(Settings.debug) e.printStackTrace();
			return new ArrayList<String>();
		}
	}

	@Override
	public void addHelper(String name, Plot plot) {
		CompletableFuture.runAsync(() -> {
			try {
				Document now = getDocument(COLLECTION_PLOTS, plot.getFullID());
				List<String> helpers = new ArrayList<String>();
				helpers.add(name);
				if(now.get(Entries.HELPERS.getValue()) != null) {
					List<String> helpersOld = (List<String>) now.get(Entries.HELPERS.getValue());
					helpersOld.forEach(helper ->{
						helpers.add(helper);
					});
					replaceProperty(COLLECTION_PLOTS, plot.getFullID(), Entries.HELPERS.getValue(), helpers);
				} else {
					addPropertyToDocument(COLLECTION_PLOTS, plot.getFullID(), new Object[][] {
						{Entries.HELPERS.getValue(), helpers}
					});
				}
				
			} catch (Exception e) {
				if(Settings.debug) e.printStackTrace();
			}
		});
	}

	@Override
	public void addMember(String name, Plot plot) {
		CompletableFuture.runAsync(() -> {
			try {
				Document now = getDocument(COLLECTION_PLOTS, plot.getFullID());
				List<String> members = new ArrayList<String>();
				members.add(name);
				if(now.get(Entries.MEMBERS.getValue()) != null) {
					List<String> membersOld = (List<String>) now.get(Entries.MEMBERS.getValue());
					membersOld.forEach(member ->{
						members.add(member);
					});
					replaceProperty(COLLECTION_PLOTS, plot.getFullID(), Entries.MEMBERS.getValue(), members);
				} else {
					addPropertyToDocument(COLLECTION_PLOTS, plot.getFullID(), new Object[][] {
						{Entries.MEMBERS.getValue(), members}
					});
				}
				
			} catch (Exception e) {
				if(Settings.debug) e.printStackTrace();
			}
		});
	}

	@Override
	public void addDenied(String name, Plot plot) {
		CompletableFuture.runAsync(() -> {
			try {
				Document now = getDocument(COLLECTION_PLOTS, plot.getFullID());
				List<String> denieds = new ArrayList<String>();
				denieds.add(name);
				if(now.get(Entries.DENIED.getValue()) != null) {
					List<String> deniedsOld = (List<String>) now.get(Entries.DENIED.getValue());
					deniedsOld.forEach(denied ->{
						denieds.add(denied);
					});
					replaceProperty(COLLECTION_PLOTS, plot.getFullID(), Entries.DENIED.getValue(), denieds);
				} else {
					addPropertyToDocument(COLLECTION_PLOTS, plot.getFullID(), new Object[][] {
						{Entries.DENIED.getValue(), denieds}
					});
				}
				
			} catch (Exception e) {
				if(Settings.debug) e.printStackTrace();
			}
		});
	}

	@Override
	public void removeHelper(String name, Plot plot) {
		CompletableFuture.runAsync(() -> {
			try {
				Document now = getDocument(COLLECTION_PLOTS, plot.getFullID());
				if(now.get(Entries.HELPERS.getValue()) != null) {
					List<String> helpersOld = (List<String>) now.get(Entries.HELPERS.getValue());
					helpersOld.remove(name);
					replaceProperty(COLLECTION_PLOTS, plot.getFullID(), Entries.HELPERS.getValue(), helpersOld);
				} 
			} catch (Exception e) {
				if(Settings.debug) e.printStackTrace();
			}
		});
	}

	@Override
	public void removeMember(String name, Plot plot) {
		CompletableFuture.runAsync(() -> {
			try {
				Document now = getDocument(COLLECTION_PLOTS, plot.getFullID());
				if(now.get(Entries.MEMBERS.getValue()) != null) {
					List<String> membersOld = (List<String>) now.get(Entries.MEMBERS.getValue());
					membersOld.remove(name);
					replaceProperty(COLLECTION_PLOTS, plot.getFullID(), Entries.MEMBERS.getValue(), membersOld);
				} 
			} catch (Exception e) {
				if(Settings.debug) e.printStackTrace();
			}
		});
	}

	@Override
	public void removeDenied(String name, Plot plot) {
		CompletableFuture.runAsync(() -> {
			try {
				Document now = getDocument(COLLECTION_PLOTS, plot.getFullID());
				if(now.get(Entries.HELPERS.getValue()) != null) {
					List<String> deniedOld = (List<String>) now.get(Entries.HELPERS.getValue());
					deniedOld.remove(name);
					replaceProperty(COLLECTION_PLOTS, plot.getFullID(), Entries.HELPERS.getValue(), deniedOld);
				} 
			} catch (Exception e) {
				if(Settings.debug) e.printStackTrace();
			}
		});
	}

	@Override
	public void setHome(Plot plot, Location location) {
		CompletableFuture.runAsync(() -> {
			try {
				Document now = getDocument(COLLECTION_PLOTS, plot.getFullID());
				String locationString = location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
				if(now.get(Entries.HOME.getValue()) != null) {
					replaceProperty(COLLECTION_PLOTS, plot.getFullID(), Entries.HOME.getValue(), locationString);
				} else {
					addPropertyToDocument(COLLECTION_PLOTS, plot.getFullID(), new Object[][] {
						{Entries.HOME.getValue(), locationString}
					});
				}
			} catch (Exception e) {
				if(Settings.debug) e.printStackTrace();
			}
		});
	}

	@Override
	public void deleteHome(Plot plot) {
		CompletableFuture.runAsync(() -> {
			try {
				replaceProperty(COLLECTION_PLOTS, plot.getFullID(), Entries.HOME.getValue(), null);
			} catch (Exception e) {
				if(Settings.debug) e.printStackTrace();
			}
		});
	}

	@Override
	public Location getHome(Plot plot) {
		try {
			Document now = getDocument(COLLECTION_PLOTS, plot.getFullID());
			if(now.getString(Entries.HOME.getValue()) != null) {
				String ex[] = now.getString(Entries.HOME.getValue()).split(":");
				return new Location(Double.parseDouble(ex[0]), Double.parseDouble(ex[1]), Double.parseDouble(ex[2]), Double.parseDouble(ex[3]), Double.parseDouble(ex[4]), Server.getInstance().getLevelByName(plot.getLevelName()));
			}
		} catch (Exception e) {
			if(Settings.debug) e.printStackTrace();
		}
		return null;
	}

	@Override
	public Plot getPlot(String name, Object number, Object level) {
		int i = 1;
        if(number != null && (int) number > 0) {
            i = (int) number;
        }
        List<Plot> plots = new ArrayList<>();
        if(level != null) {
            for (String plot : getPlots(name, level)) {
                String[] ex = plot.split(";");
                plots.add(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]));
            }

        } else {
            for (String plot : getPlots(name, null)) {
                String[] ex = plot.split(";");
                plots.add(new Plot(Integer.parseInt(ex[1]), Integer.parseInt(ex[2]), ex[0]));
            }
        }
        if((i - 1) >= plots.size()) return null;
        if(plots.size() > 0) {
            Plot plot = plots.get(i - 1);
            return plot;
        }
        return null;
	}

	@Override
	public List<String> getPlots(String name, Object level) {
		List<String> plots = new ArrayList<>();
        if(level != null) {
            try {
                for(Document doc : getAllDocuments(COLLECTION_PLOTS)) {
                	if(doc.getString(Entries.LEVEL.getValue()).equals(level)) {
                        String[] plotid = doc.getString(Entries.PLOT_ID.getValue()).split(";");
                        plots.add(level + ";" + plotid[0] + ";" + plotid[1]);
                    }
                }
            } catch (Exception e) {
                if(Settings.debug) e.printStackTrace();
            }
        } else {
            try {
            	for(Document doc : getAllDocuments(COLLECTION_PLOTS)) {
            		String[] plotid = doc.getString(Entries.PLOT_ID.getValue()).split(";");
                    plots.add(level + ";" + plotid[0] + ";" + plotid[1]);
                }
            } catch (Exception e) {
                if(Settings.debug) e.printStackTrace();
            }
        }
        return plots;
	}

	@Override
	public Plot getNextFreePlot(String level) {
		List<Plot> plots = new ArrayList<>();
		try {
			
        	for(Document doc : getAllDocuments(COLLECTION_PLOTS)) {
        		if(doc.getString(Entries.LEVEL.getValue()).equals(level)) {
        			String[] plotid = doc.getString(Entries.PLOT_ID.getValue()).split(";");
                    plots.add(new Plot(Integer.parseInt(plotid[0]), Integer.parseInt(plotid[1]), level));
        		}
            }
        	
        	if (plots.size() == 0) return new Plot(0, 0, level);
            int lastX = 0;
            int lastZ = 0;

            for (Plot plot : plots) {
                int x = plot.getX() - lastX;
                int y = plot.getZ() - lastZ;
                int diff = Math.abs(x * y);
                if (diff < 4) {
                    lastX = plot.getX();
                    lastZ = plot.getZ();

                    Plot find = new Plot(plot.getX() + 1, plot.getZ(), level);
                    if (!hasOwner(find)) return find;
                    find = new Plot(plot.getX(), plot.getZ() + 1, level);
                    if (!hasOwner(find)) return find;
                    find = new Plot(plot.getX() - 1, plot.getZ(), level);
                    if (!hasOwner(find)) return find;
                    find = new Plot(plot.getX(), plot.getZ() - 1, level);
                    if (!hasOwner(find)) return find;
                }
            }
            return getNextFreePlot(level);
        } catch (Exception e) {
            if(Settings.debug) e.printStackTrace();
        }
		return null;
	}

	protected void establishConnectionAndRun() {
		connectClient(databaseConnectionString);
		Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(FuturePlots.getInstance(), () -> {
			if(!connectionEstablished())
				connectClient(databaseConnectionString);
		}, 0, 20*60);
	}

	private Document buildDocument(String documentName, Object[][] keyValueCollection) {
		Document document = new Document(Entries.PLOT_ID.getValue(), documentName);
		for (Object[] append : keyValueCollection) {
			document = document.append((String) append[0], append[1]);
		}
		return document;
	}
	
	private void insertDocument(String collection, Document document) {
        mongoClient.getDatabase(databaseName).getCollection(collection).insertOne(document);
    }
	
	private void replaceDocument(String collection, String documentName, Document newDocument) {
    	getCollection(collection).replaceOne(getDocument(collection, documentName), newDocument);
    }
	
	private void addPropertyToDocument(String collection, String documentName, Object[][] propertiesToAdd) {
        Document toUpdateDocument = getDocument(collection, documentName);
        for (Object[] append : propertiesToAdd) {
            toUpdateDocument.append((String) append[0], append[1]);
        }
        replaceDocument(collection, documentName, toUpdateDocument);
    }
	
	private void replaceProperty(String collection, String documentName, String property, Object newvalue) {
        Document document = getDocument(collection, documentName);
        Bson filter = document;
        Document newDocument = new Document(Entries.PLOT_ID.getValue(), documentName);
        for (Map.Entry<String, Object> entry : document.entrySet()) {
            if (entry.getKey().equals(property)) {
                if (entry.getKey().equals(document.get("_id"))) continue;
                newDocument.append(entry.getKey(), newvalue);
            } else {
                if (entry.getKey().equals(document.get("_id"))) continue;
                newDocument.append(entry.getKey(), entry.getValue());
            }
        }
        getCollection(collection).updateOne(filter, newDocument);
    }
	
	private MongoCollection<Document> getCollection(String collectionName) {
        if (mongoClient.getDatabase(databaseName).getCollection(collectionName) != null) {
            return mongoClient.getDatabase(databaseName).getCollection(collectionName);
        }
        return null;
    }
	
	private Document getDocument(String collection, String documentName) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(Entries.PLOT_ID.getValue(), documentName);
        FindIterable<Document> cursor = mongoClient.getDatabase(databaseName).getCollection(collection).find(whereQuery);
        return cursor.first();
    }
	
	public Document getDocument(String collection, String byKey, Object whereValue) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(byKey, whereValue);
        FindIterable<Document> cursor = mongoClient.getDatabase(databaseName).getCollection(collection).find(whereQuery);
        return cursor.first();
    }
	
	public boolean deleteOne(String collection, Object[][] query) {
        BasicDBObject whereQuery = new BasicDBObject();
        for(Object obj[] : query) {
        	whereQuery.put((String)obj[0], obj[1]);
        }
        FindIterable<Document> cursor = mongoClient.getDatabase(databaseName).getCollection(collection).find(whereQuery);
        getCollection(collection).deleteOne(cursor.first());
        return true;
    }
	
	public boolean deleteOne(String collection, String documentName) {
        BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(Entries.PLOT_ID.getValue(), documentName);
        FindIterable<Document> cursor = mongoClient.getDatabase(databaseName).getCollection(collection).find(whereQuery);
        getCollection(collection).deleteOne(cursor.first());
        return true;
    }
	
	public boolean connectionEstablished() {
		return getDatabase(databaseName) != null;
    }
	
	public void connectClient(String connectionString) {
        try {
        	mongoClient = new MongoClient(new MongoClientURI(connectionString));
        } catch(Exception ex) {ex.printStackTrace();}
    }

    public MongoDatabase getDatabase(String dbName) {
        return mongoClient.getDatabase(dbName);
    }
    
    public FindIterable<Document> getAllDocuments(String collectionName) {
        return getCollection(collectionName).find();
    }
    
    private enum Entries {
    	LEVEL("level"), PLOT_ID("plotid"), HOME("home"), OWNER("owner"), HELPERS("helpers"), MEMBERS("members"), DENIED("denied"), FLAGS("flags"), MERGE("merge"), MERGES("merges")
    	;
    	private String value;
    	Entries(String value) {
    		this.value = value;
    	}
    	public String getValue() {
			return value;
		}
    }

}
