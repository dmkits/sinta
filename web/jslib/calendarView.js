/**
 * Created by ianagez on 30.12.2016.
 */
define(["dojo/_base/declare", "dojox/mobile/View","dojox/mobile/Heading", "dojox/mobile/ToolBarButton", "dijit/CalendarLite"],
    function(declare, View,Heading, ToolBarButton, CalendarLite) {
        return declare("CalendarView", [View], {

            constructor: function(args,parentName){
                declare.safeMixin(this,args);

            },

            postCreate: function(){
                //this.calendar_view = new View({id: this.id});
                this.hide();
                document.getElementById("body").appendChild(this.domNode);

                this.calendar_heading = new Heading({
                    transition: "none",
                    id: "calendar_heading",
                    back: "Назад"
                });
                this.addChild(this.calendar_heading);

                this.date_picker = new CalendarLite({lang: 'ru',id: "date_picker"});
                this.addChild(this.date_picker);
                this.date_picker.startup();

                var accept_heading = new Heading();
                var accept_btn = new ToolBarButton({id: "accept_btn", label: "Выбрать",
                    style: "width:75px;align",transition: "none"});
                accept_heading.addChild(accept_btn);
                this.addChild(accept_heading);
                accept_heading.startup();

                this.startup();

                //var string_date = "" + btn.get("label").substring(6, 10) + "-" + btn.get("label").substring(3, 5) + "-" + btn.get("label").substring(0, 2);
                //date_picker.set("value", string_date);
                //calendar_heading.set("label", heading);
                //calendar_heading.set("moveTo", view.id);
                //accept_btn.set("moveTo", view.id);
                //accept_btn.onClick = function () {
                //    var selected_date = moment(date_picker.get("value"));
                //    btn.set("label", selected_date.format("DD.MM.YYYY"));
                //    if (btn.id == "date_first_" + view.id) {
                //        view.btnBeginDate_dateValue = selected_date;
                //    }
                //    if (btn.id == "date_last_" + view.id) {
                //        view.btnEndDate_dateValue = selected_date;
                //    }
//                        var date1_milsec = new Date(view.btnBeginDate_dateValue).getTime();
//                        var date2_milsec = new Date(view.btnEndDate_dateValue).getTime();
//                        if ((date2_milsec - date1_milsec) < 0) {
//                          var temp=view.btnBeginDate_dateValue;
//                            view.btnBeginDate_dateValue=view.btnEndDate_dateValue;
//                            view.btnEndDate_dateValue=temp;
//                        }
   //                 view.loadDetailContent();
            },

            setContent: function(parentView){
                this.calendar_heading.set("moveTo", parentView.id);
            }
        });
    });