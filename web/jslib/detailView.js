/**
 * Created by ianagez on 05.01.2017.
 */
/**
 * Created by ianagez on 30.12.2016.
 */
define(["dojo/_base/declare", "dojox/mobile/View", "dojox/mobile/Heading", "dojox/mobile/ScrollableView", "dojox/mobile/RoundRectList",
        "dojox/mobile/ListItem", "dojox/mobile/ToolBarButton", "dojox/mobile/ProgressIndicator"],
    function (declare, View, Heading, ScrollableView, RoundRectList, ListItem, ToolBarButton, ProgressIndicator) {
        return declare("PickUnitView", [View], {

            constructor: function (args, parentName) {
                declare.safeMixin(this, args);
            },

            id: "",
            actionValue: "",
            heading_label: "",
            view_main: "",
            unit_id: "",
            pickUnitView: "",
            url: "",

            postCreate: function () {
                document.getElementById('body').appendChild(this.domNode);
                var instance = this;
                this.top_heading = new Heading({
                    label: instance.heading_label,
                    back: "Назад",
                    moveTo: "view_main",
                    transition: "none"
                });
                this.addChild(this.top_heading, 0);

                this.inner_scroll = new ScrollableView({});
                this.setPickBtnFor(this.view_main, this.pickUnitView, this.unit_id);
                this.addChild(this.inner_scroll);
            },
            setPickBtnFor: function (view_main, pickUnitView, unit_id) {
                var instance = this;
                if (!this.pickUnitBtnHeading) {
                    this.pickUnitBtnHeading = new Heading();
                    var icon_btn = new ToolBarButton({
                        preventTouch: true,
                        clickable: false,
                        icon: "/icons/hearts29x29.ico",
                        iconPos: "0,0,29,29"
                    });
                    this.pickUnitBtnHeading.addChild(icon_btn);
                    this.addChild(this.pickUnitBtnHeading, 1);
                    this.pickUnitBtnHeading.startup();
                }
                if (!this.pickUnitBtn) {
                    var v = window.innerWidth - 90;
                    this.pickUnitBtn = new ToolBarButton({
                        style: "width:" + v + "px; font-size:14px; align: center",
                        transition: "none",
                        moveTo: "pick_unit_view",
                        onClick: function () {
                            instance.pickUnitView.setContent(this, instance);
                        }
                    });
                    this.pickUnitBtn.selected_units = [];
                }
                this.pickUnitBtn.units_data = view_main.pickUnitBtn.units_data;
                if (unit_id === undefined) {
                    this.pickUnitBtn.selected_units = view_main.pickUnitBtn.selected_units;
                    this.pickUnitBtn.set("label", view_main.pickUnitBtn.label);
                } else {
                    this.pickUnitBtn.selected_units = [];
                    for (var u in view_main.pickUnitBtn.units_data) {
                        var unit_data = view_main.pickUnitBtn.units_data[u];
                        if (unit_id == unit_data.id) {
                            this.pickUnitBtn.selected_units[0] = unit_data;
                            this.pickUnitBtn.set("label", unit_data.short_name);
                            break;
                        }
                    }
                }
                this.pickUnitBtnHeading.addChild(this.pickUnitBtn);

                window.onresize = function () {
                    instance.pickUnitBtn.set("style", "width:" + v + "px; font-size:14px; align: center");
                };
            },
            setDetailContent: function (data) {
                this.list_items = new RoundRectList({"id": "list" + this.id, style: "position:relative"});
                this.inner_scroll.addChild(this.list_items);
                this.inner_scroll.scrollTo({x: 0, y: 0});
                for (var i in data) {
                    var dataItem = data[i];
                    var item = new ListItem({
                        style: "background-color:white",
                        label: dataItem.label,
                        rightText: numeral(dataItem.value).format('0,0')
                    });
                    this.list_items.addChild(item);
                }
                this.list_items.startup();
            },

            loadDetailContent: function (view_main, unit_id) {
                if (this.list_items) this.list_items.destroy();
                if (this.prog) this.prog.destroy();
                this.prog = new ProgressIndicator({size: 200, center: true, removeOnStop: true});
                document.getElementById('body').appendChild(this.prog.domNode);
                this.prog.start();

                var actionValue = this.actionValue;
                var bdate = this.btnBeginDate.dateValue.format("YYYYMMDD"), edate = this.btnEndDate.dateValue.format("YYYYMMDD");
                var unit_params = null;
                if (unit_id == undefined) {
                    var units_list = this.pickUnitBtn.selected_units;
                    for (var i in units_list) {
                        var unit_condition = "unit_" + i + "_id=" + units_list[i].id;
                        unit_params = (unit_params === null) ? unit_condition : unit_params + "&" + unit_condition;
                    }
                } else
                    unit_params = "unit_id=" + unit_id;
                var instance = this;

                getJSONData({//  url: "mobile/cashin_sum.json"
                        url: this.url,
                        condition: "action=" + actionValue + "&bdate=" + bdate + "&edate=" + edate + "&" + unit_params,
                        consoleLog: true
                    }

                    , function (success, result) {
                        if (success) {
                            if (result.error) {
                                view_main.msgBox.innerHTML = "Нет данных";
                                console.log("loadDetailContentForView getJSONData DATA ERROR! error=", result.error);
                                view_main.dialogWin.show();
                            } else instance.setDetailContent(result.items);
                        } else {
                            view_main.msgBox.innerHTML = "Нет связи с сервером";
                            view_main.dialogWin.show();
                        }
                        instance.prog.stop();
                    }
                );
            }
        });
    });

