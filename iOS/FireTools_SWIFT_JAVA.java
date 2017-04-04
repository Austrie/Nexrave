class FireTools {

    var ref: FIRDatabaseReference!
    ref = FIRDatabase.database().reference();
    let user = FIRAuth.auth()?.currentUser;
    let facebook_id;

    setFacebookId() {
        var userRef = ref.child("users").child(user.uid).child("facebook_id");
        userRef.observe(of: .value, with: { (snapshot) in
                if (dataSnapshot.exists()) {
                    facebook_id = dataSnapshot.value;
                } else {
                    //User has no events
                    print("No events");
                }
        }) { (error) in
            print(error.localizedDescription);
        }
    }

    loadFeedEvents() {
        setFacebookId();

        var userRef = ref.child("users").child(user.uid)
                .child("accepted_invites");

        pullHostingEvents();
        pullOrganizationEvents();

        userRef.observe(of: .value, with: { (snapshot) in
                if (dataSnapshot.exists()) {
                    var map = dataSnapshot.value as? [String : AnyObject] ?? [:];

                    //TODO: Order by event time
                    var invitedEventsList =  Array<AnyObject>;
                    invitedEventsList.append(map.allValues);
                    search(invitedEventsList, userRef);
                } else {
                    //User has no events
                    print("No events");
                }
        }) { (error) in
            print(error.localizedDescription);
        }
    }

    pullHostingEvents() {
        var userRef2 = ref.child("users").child(user.uid).child("hosting_events");

        userRef2.observe(of: .value, with: { (snapshot) in
                if (dataSnapshot.exists()) {
                    var map = dataSnapshot.value as? [String : AnyObject] ?? [:];

                    //TODO: Order by event time
                    var invitedEventsList = Array<AnyObject>;
                    invitedEventsList.append(map.allValues);
                    search(invitedEventsList, userRef2);
                } else {
                    //User has no events
                    print("No events");
                }
        }) { (error) in
            print(error.localizedDescription);
        }
    }

    pullOrganizationEvents() {
        var userRef2 = ref.child("users").child(user.uid).child("subscribed").child("organizations");

        userRef2.observe(of: .value, with: { (snapshot) in
                if (dataSnapshot.exists()) {
                    var map = dataSnapshot.value as? [String : AnyObject] ?? [:];

                    //TODO: Order by event time
                    var orgsList = Array<AnyObject>;
                    orgsList.append(map.allValues);
                    var i = 0;
                    while (i < orgsList.size()) {
                        var orgEventsList = ref.child("organizations").child(String(describing: orgsList.get(i))).child("events");
                        orgEventsList.observe(of: .value, with: { (snapshot) in
                            if (dataSnapshot.exists()) {
                                var map2 = dataSnapshot.value as? [String : AnyObject] ?? [:];
                                var eventsList = new Array<Object>;
                                eventsList.append(map2.values());
                                search(eventsList, orgEventsList);
                                i++;
                            } else {
                                //User has no events
                                print("No events");
                            }
                        }) { (error) in
                            print(error.localizedDescription);
                    }
                } else {
                    //User has no events
                    print("No events");
                }
        }) { (error) in
            print(error.localizedDescription);
            }

    }

    search(invitedEventsList, final DatabaseReference userRef2) {

        var eventsRef = ref.child("events");
        var i = 0;
        while (i < invitedEventsList.size()) {
            var event_id = String(description: invitedEventsList.get(i));
            var eventRef = eventsRef.child(event_id);
            eventRef.keepSynced(true);

            eventRef.observe(of: .value, with: { (snapshot) in
                if (dataSnapshot.exists()) {
                    var event = dataSnapshot.value as? Event;
                    item.event_id = event_id;
                    //Adds the firebase id in case the user was added via facebook
                    eventRef.child("guests").child(facebook_id)
                                .child("firebase_id").setValue(user.uid);

                        //Uncomment the sort fucntion once you complete it
                        //sort(feedItems, item, listAdapter);
                    } else {
                        //This event no longer exists
                        print("This event no longer exists");
                        userRef2.child(event_id).removeValue();

                    }
            }) { (error) in
                print(error.localizedDescription);
            }
            i++;
        }
    }

    /**
     * Sorts the events in the list by time and date
     * @param  {[type]} ArrayListEvents<Event> feedItems     The custom arraylist that holds the list of events
     * @param  {[type]} Event                  item          An Event object
     * @param  {[type]} FeedListAdapter        listAdapter   The object that keeps the feed list updated
     *
     * LEAVING THIS HERE, BECAUSE I'M NOT SURE WHAT CODE YOURE GOING TO USE FOR THIS
     */
    // public static void sort(ArrayListEvents<Event> feedItems, Event item, FeedListAdapter listAdapter) {
    //     feedItems.add(item);
    //     Collections.sort(feedItems, new Comparator() {
    //         @Override
    //         public int compare(Object o, Object t1) {
    //             if (Long.valueOf(((Event) o).date_time.replace(".", ""))
    //                     > (Long.valueOf(((Event) t1).date_time.replace(".", "")))) {
    //                 return 1;
    //             }

    //             if (Long.valueOf(((Event) o).date_time.replace(".", ""))
    //                     < (Long.valueOf(((Event) t1).date_time.replace(".", "")))) {
    //                 return -1;
    //             }
    //             return 0;
    //         }
    //     });
    //     // notify data changes to list adapter
    //     listAdapter.notifyDataSetChanged();
    // }

}