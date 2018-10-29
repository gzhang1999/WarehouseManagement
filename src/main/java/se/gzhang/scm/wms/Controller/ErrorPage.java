/**
 * Copyright 2018
 *
 * @author gzhang
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.gzhang.scm.wms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import se.gzhang.scm.wms.authorization.model.User;

@Controller
public class ErrorPage {


    @RequestMapping(value="/error/accessfail", method = RequestMethod.GET)
    public ModelAndView displayAccessFail(@RequestParam("url") String url) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("url", url);
        modelAndView.setViewName("AccessFail");
        return modelAndView;
    }
}
