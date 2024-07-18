#!/usr/bin/env python

import xml.etree.ElementTree as ET
import urllib.request

def css_styles(countries):
    style = ET.Element('style')
    style.text = f'''
    {','.join([f'#partner_map #{c}' for c in countries])} {{
        fill: #a32020;
    }}
    {','.join([f'#partner_map #{c}:hover' for c in countries])} {{
        fill: #c62626;
    }}
    #partner_map g:hover {{
        fill: #b5b5b8;
    }}
    #partner_map a:visited {{
        fill: unset;
    }}
    '''
    return style

def nest_in_anchor(country_id, children):
    anchor = ET.Element('a', {'xlink:href': f"#{country_id}"})
    anchor.extend(children)
    return anchor

def export_map(outfile, highlighted_countries):
    svg_src = "https://upload.wikimedia.org/wikipedia/commons/a/ad/BlankMap-World_gray.svg"

    # make default namespace 
    ET.register_namespace('', "http://www.w3.org/2000/svg")
    ET.register_namespace('xlink', "http://www.w3.org/1999/xlink")

    with urllib.request.urlopen(svg_src) as res:
        tree = ET.parse(res)

    root = tree.getroot()
    root.set('xmlns:xlink', "http://www.w3.org/1999/xlink") # this gets stripped when we parse so add it back in

    # set attributes on the root svg elem so it nicely embeds and scales
    root.set('viewbox', f"0 0 {root.get('width')} {root.get('height')}")
    root.set('height', '30vh')
    root.set('width', '100%')
    root.set('id', 'partner_map')

    country_nodes = root[0] # these are g(roups) with id attributes
    for child in country_nodes:
        if len(child) > 0:
            country_id = child.get('id', "default")
            if country_id in highlighted_countries:
                country_paths = [c for c in child]
                list(map(child.remove, country_paths))
                assert len(child) == 0
                child.append(nest_in_anchor(country_id.upper(), country_paths))

    root.insert(0, css_styles(highlighted_countries))

    tree.write(outfile)


if __name__ == "__main__":
    partner_countries = ['us', 'jp', 'kr', 'de', 'at', 'ch', 'sg', 'ca', 'tw', 'in', 'mx']
    export_map("layouts/shortcodes/partner_map.html", partner_countries)

    
