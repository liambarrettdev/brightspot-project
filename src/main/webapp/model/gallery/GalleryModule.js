import $ from 'jquery'
import bsp_utils from 'bsp-utils'

export class GalleryModule {

    constructor(el) {
        var self = this

        self.current = 1

        self.init()
    }

    init() {
        var self = this

        self.showSlide(self.current);

        $('.prev').click(function(e) {
            self.showSlide(self.current -= 1)
        })

        $('.next').click(function(e) {
            self.showSlide(self.current += 1)
        })
    }

    showSlide(index) {
        var self = this

        let galleryIndex = $('.GalleryModule-index')
        let slides = $('.Slide')

        if (index > slides.length) {
            self.current = 1
        }

        if (index < 1) {
            self.current = slides.length
        }

        var temp = self.current - 1
        slides.each(function() {
            $( this ).css('transform', `translateX(${-100 * temp}%)`)
        });
        galleryIndex.html(self.current + ' / ' + slides.length)
    }

    get current() {
        return this._currentSlide
    }

    set current(value) {
        this._currentSlide = value
    }
}

export default bsp_utils.plugin(false, 'bsp', 'gallery-module', {
    '_each': function (item) {
        new GalleryModule($(item))
    }
});
