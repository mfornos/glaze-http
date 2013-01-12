(function ($) {
    $.fn.tabify = function () {
        return this.each(function () {
            var tabs = $(this);
            $("div", tabs).hide();
            $("ul li:first", tabs).addClass("active");
            $("div:first", tabs).show();

            var tabLinks = $("ul li", tabs);

            $(tabLinks).click(function () {
                $(tabLinks).removeClass("active");
                $(this).addClass("active");
                $("div", tabs).hide();

                var activeTab = $(this).find("a").attr("href");
                $(activeTab).show();
                return false;
            });
        });
    };
})(jQuery);
