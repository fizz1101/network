var networkCache = {};
var routeCache = {};
$(window).load(function(){
    //初始化数据加载
    init_data();

    //变更网卡类型
    $("#networkType").change(function() {
        var type = $(this).val();
        setNetworkInfo(type);
        $('.change').attr("disabled", true);
    })

    //编辑点击
    $("#revise").click(function(){
        $('.change').attr("disabled",false);
    });

    //网络保存点击事件
    $("#btn_network_save").click(function () {
        save_network();
        $('.change').attr("disabled", true);
    })

    //路由保存点击事件
    $("#btn_route_save").click(function () {
        save_route();
    })

    //新增框
    $("#close").click(function(){
        $('.new').css("display",'none');
        $('.close').css("display",'none');
    });
    $("#new-built").click(function(){
        $('.new').css("display",'block');
        $('.close').css("display",'block');
    });

    //选中数据
    $(".row").click(function () {
        var chks = $("input[type='checkbox']",this);
        var tag = $(this).attr("tag");
        if(tag =="selected"){
            // 之前已选中，设置为未选中
            $(this).attr("tag","");
            chks.prop("checked",false);
        }else{
            // 之前未选中，设置为选中
            $(this).attr("tag","selected");
            chks.prop("checked",true);
        }
    });

    //选中删除数据
    $("#amputate").click(function() {
        $("input[name='test']:checked").each(function() { // 遍历选中的checkbox
            n = $(this).parents("tr").index();  // 获取checkbox所在行的顺序
            $("table#route_table").find("tr:eq("+n+")").remove();
        });
    });


    /**
     * 初始化数据加载
     */
    function init_data() {
        networkType();
        networkList();
        routeList();
    }

    /**
     * 获取网卡类型
     */
    function networkType() {
        $.ajax({
            url: request.url.network_type,
            type: "post",
            dataType: "json",
            success: function(data) {
                if (data!=null && data.code=="0") {
                    setNetworkType(data.result);
                } else {
                    alert(data.msg);
                }
            },
            error: function(data) {
                alert("获取网卡类型，服务器交互异常");
            }
        })
    }

    /**
     * 设置网卡类型
     */
    function setNetworkType(data) {
        var option = "";
        for (var key in data) {
            option += "<option value='"+data[key].key+"'>"+data[key].value+"</option>";
        }
        $("#networkType").html(option);
    }

    /**
     * 获取网卡信息列表
     */
    function networkList() {
        $.ajax({
            url: request.url.network_list,
            type: "post",
            dataType: "json",
            success: function(data) {
                if (data!=null && data.code=="0") {
                    cacheNetworkList(data.result);
                    setNetworkInfo("ens32");
                } else {
                    alert(data.msg);
                }
            },
            error: function(data) {
                alert("获取网络配置列表，服务器交互异常");
            }
        })
    }

    /**
     * 缓存网卡信息列表
     */
    function cacheNetworkList(data) {
        for (var key in data) {
            networkCache[key] = data[key];
        }
    }

    /**
     * 更新网卡信息缓存
     */
    function updateNetworkCache(data) {
        var device = data.DEVICE;
        // delete data["DEVICE"];
        networkCache[device] = data;
    }

    /**
     * 设置网卡信息
     */
    function setNetworkInfo(type) {
        var info = networkCache[type];
        if (info!=null && info != "undefined") {
            $("#ip").val(info.IPADDR);
            $("#netmask").val(info.NETMASK);
            $("#gateway").val(info.GATEWAY);
        }
    }

    /**
     * 保存网络配置
     */
    function save_network() {
        var data = $("#networkForm").serialize();
        $.ajax({
            url: request.url.network_save,
            type: "post",
            data: data,
            dataType: "JSON",
            success: function(data) {
                if (data!=null) {
                    alert(data.msg);
                    updateNetworkCache(data.result);
                } else {
                    alert("服务器异常");
                }
            },
            error: function(data) {
                alert("保存网络配置，服务器交互异常");
            }
        });
    }

    /**
     * 获取路由信息
     */
    function routeList() {
        $.ajax({
            url: request.url.route_list,
            type: "post",
            dataType: "json",
            success: function() {

            },
            error: function () {
                alert("获取路由配置，服务器交互异常");
            }
        })
    }

    /**
     * 保存路由配置
     */
    function save_route() {
        $.ajax({
            url: request.url.route_save,
            type: "post",
            dataType: "json",
            success: function() {

            },
            error: function () {
                alert("保存路由配置，服务器交互异常");
            }
        })
    }

});



