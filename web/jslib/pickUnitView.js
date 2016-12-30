/**
 * Created by ianagez on 30.12.2016.
 */
define([],
    function(){
        return {
            content:"PickUnitView",

            myalert: function(msg){
                alert(msg);
            },
            myalert2: function(){
                alert(this.content);
            }
        };
    });