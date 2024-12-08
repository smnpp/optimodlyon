import React, { useRef } from 'react';
import { Button, ButtonColor } from './button';

interface FileDialogProps {
    logo?: string;
}

const FileDialog: React.FC<FileDialogProps> = ({ logo }) => {
    const ref = useRef<HTMLInputElement>(null);
    const handleClick = (e?: React.MouseEvent<HTMLButtonElement>) => {
        if (ref.current) {
            ref.current.click();
        }
    };

    return (
        <>
            <Button
                onClick={handleClick}
                text="Upload Map"
                color={ButtonColor.red}
                logo={logo} // Ensure this path is correct
                children={
                    <input ref={ref} type="file" style={{ display: 'none' }} />
                }
            />
        </>
    );
};

export default FileDialog;
